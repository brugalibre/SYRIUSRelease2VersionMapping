#!/bin/bash

###############################################################################################################
#					Read-me    	     						      #
# Das Skript muss mit der Angabe zu einem API-Bridge-Syrius-integration Branch gestartet werden,  	      #
# Anschliessend ermittelt es über alle Syr-Int. Branches anhand dem gradle.properties-File die jeweilige      #	
# API-Bridge version. Diese Informationen werden dann in einem csv-File (zwischen-) gespeichert		      #
#													      #  
###############################################################################################################


# Variablen für die zu prüfenden Branches. Je nach dem welche Branches geprüft werden sollen, gilt es hier Anpassungen zu machen
relevantBranches='master|rel3_[0-9][0-9]_([0-9]+|(HEAD))'
notRelevantBranches='feature|bug|rel3_08' # Keine feature/bugfix branches & keine rel3_08er!

# File in dem das Mapping gespeichert wird
apiBridge2SyrReleaseFile=$1

# Auslesen des reinen Branchnamen. Dem branchname steht jeweils ein 'refs/remotes/origin/' davor, das würden wir gerne weg machen
function getSyriusRel(){
  branch=$1
  echo $branch | cut -d '/' -f 4
}

# Auslesen der API-Bridge via git show kompletten Inhalt vom gradle.properties anzeigen und nach der spezifischen SpecVersion vom fachmodul greppen
# und nur dieses behalten (Parameter -oP) -> (Ergebnis: 'apibridgeBestandsverwSpecVersion="1.0.0'). Die Common-Spec ignorieren
# -> Mit 'sed -r' und Regex den Buchstaben-Teil vom String replacen und so die reine Version extrahieren -> 1.0.0 oder 1.0.0-RC-5 oder 5.0.0-ALPHA-5
# -> Replace: Mit dem Zusatz -r kann irgen ein Pattern mit einem anderen String ersetzt werden -> 'sed -r s/(pattern)/ersatz/'. 
#	 Ohne -r kann anstelle von (pattern) direkt der zu ersetzende Wert eingetragen werden
function readAPIBridgeVersion(){
  file=$1
  branch=$2
  apiBridgeFachmodulSpecVersionPattern='(^(apibridge)((?!Common)([A-Z][a-z]+){1,})(SpecVersion)=)'
  apiBridgeVersionPattern='(([0-9]+)[.]([0-9]+)[.]([0-9]+)(((-RC-)|(-ALPHA-))([0-9])+){0,1})'
  git show $branch':'$file | grep -oP $apiBridgeFachmodulSpecVersionPattern$apiBridgeVersionPattern | sed -r 's/((^(\w)+)[=])//'
}

# Fetcht die gewünschten Git-Repositories und sucht in diesen nach dem gradle.properties. Aus diesem wird dann die, diesem API-Bridge-Integrations-Branch zugewiesene 
# API-Bridge-Version ausgelesen. Der Branch der Integration entspricht sogleich auch dem SYRIUS-Branch
function mapSyrius2ApiBridgeIntegrationRelease(){
  for branch in $(git for-each-ref --format="%(refname)" refs/remotes | grep -iE $relevantBranches | grep -vE $notRelevantBranches); do
    for file in $(git ls-tree -r --name-only $branch | grep 'gradle.properties' ); do
      apiBridgeVersion=$(readAPIBridgeVersion $file $branch)
      syrRel=$(getSyriusRel $branch)
      echo $apiBridgeVersion';'$syrRel >> $apiBridge2SyrReleaseFile
    done
  done
}

function createEmptyAPIBridge2APIBridgeIntegMappingFile(){
  rm -f $apiBridge2SyrReleaseFile
  echo "API-Bridge-Version;SYRIUS-Integration Version" >> $apiBridge2SyrReleaseFile
}

if [ -n "$2" ]; then
  cd $2 # Falls das Skript ausserhalb von einem Git-Repo aufgerufen wird, ist ein cd notwendig. Andernfalls können keine git-Befehle abgesetzt werden
fi
createEmptyAPIBridge2APIBridgeIntegMappingFile
mapSyrius2ApiBridgeIntegrationRelease
