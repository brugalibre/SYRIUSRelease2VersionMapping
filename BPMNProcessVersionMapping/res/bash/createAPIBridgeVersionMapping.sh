#!/bin/bash

###############################################################################################################
#					Read-me    	     						      #
# Das Skript muss mit der Angabe zu einem API-Bridge-Branch gestartet werden. Vorgängig muss das skript       #
# 'mapSyrius2APIBridgeRelease' zwingend gelaufen sein, damit bekannt ist, welche SYRIUS-Versionen auf welche  #
# API-Bridge (integration) zeigen.									      #
# 													      #
# Diese ermittelten API-Bridge-Versionen bestimmen, welche API-Bridge Branches relvant sind. Pro relevantem   #
# Branch werden die xsd-files ermittelt und deren Version ausgelesen. Anschliessend werden die Ergebnisse in  #
# ein csv geschrieben. 											      #
# Dieses finale csv beinhaltet den Namen des API-Bridge-Service-Files, seine version, die Version der 	      #
# API-Bridge sowie der SYRIUS-Release								   	      #
#													      #  
###############################################################################################################

# Name & Pfad vom Outputfile & vom File in dem das Mapping SYRIUS-API-Bridge gespeichert ist
apiBridgeVersionFileRawCsv=$1
apiBridge2SyrReleaseFile=$2

# Die packages von xsd's, die uns NICHT interessieren
irrelevantXsdPackages='common|code' # codes, types, identifier

function createEmptyAPIBridgeVersionFile(){
  rm -f $apiBridgeVersionFileRawCsv
  echo "API-Bridge Service;API-Bridge Service Version;API-Bridge Version;SYRIUS-Release" >> $apiBridgeVersionFileRawCsv
}

function readAllAPIBridgeVersionsAndWrite2File(){
  relevantBranches=$(getRelevantAPIBranchesNamePattern)
  for branch in $(git for-each-ref --format="%(refname)" refs/remotes | grep -iE $relevantBranches); do
    syrReleases2APIBridgeVersionen=$(getSyrReleases2APIBridgeVersionMapping4APIBridgeBranchName $branch)
    
    if [ -z "$syrReleases2APIBridgeVersionen" ]; then
      continue # Teils gibts releases, (aktuell grad der 3.4-RC / 2.7-RC) für die auf keinen SYRIUS-Rel mappen
    fi 
    for file in $(git ls-tree -r --name-only $branch | grep '.xsd' | grep -vE $irrelevantXsdPackages); do
      apiBridgeServiceVersion=$(readAPIBridgeServiceVersion $file $branch)
      apiBridgeServiceFileName=$(getSimpleFileName $file)
      
      read -a syrRelease2APIBridgeVersionenArray <<< $syrReleases2APIBridgeVersionen
      for syrRelease2APIBridgeVersion in "${syrRelease2APIBridgeVersionenArray[@]}"; do
	apiBridgeVersion=$(getAPIBridgeVersionFromLine $syrRelease2APIBridgeVersion)
	syrRelease=$(getSyrReleaseFromLine $syrRelease2APIBridgeVersion)
	echo $apiBridgeServiceFileName';'$apiBridgeServiceVersion';'$apiBridgeVersion';'$syrRelease >> $apiBridgeVersionFileRawCsv
      done
    done
  done
}

# Erstellt für einen gegebenen API-Bridge-Branchnamen ein Mapping von SYRIUS-Releasen und der dazu passenden API-Bridge Versionen
# Also z.B. für Branchnamen 3.1 wird folgendes Mapping erstellt:
# '3.1.1;rel3_xx_HEAD 3.1.0;rel3_xx_03' (Die Mapping-Päärchen sind jeweils durh ein Leerzeichen getrennt)
# Ausser beim Master-Branch kann es mehrere Treffer geben, die durch ein Leerzeichen getrennt sind
# Parameter der Methode ist der vollständige Git-Branchname, welcher als erstes gekürzt wird. Also z.B. von refs/remotes/origin/release/3.1 zu 3.1

# Pro Zeile des Mapping-Files wird die API-Brdige-Version ausgelesen und gekürzt, damit sie mit dem gekürzten Git-Branchnamen verglichen werden kann
# Grundsätzlich wäre das Kürzen nicht nötig. Für alle Release-Branches funktioniert der Vergleich mit [[ "$apiBridgeBranchName" =~ "$apiBridgeVersionFromFile" ]]
# Für alle RC-Branches bricht dieser Vergleich (4.1-RC vs. 4.1.1-RC). Evtl. kann der Vergleich jedoch noch angepasst werden, damit das auch tun würde.

# Sonderfall ist zudem einmal mehr der API-Bridge-Master-branch: Dieser ist ausserhalb Git ein a.b.c-ALPHA und nicht master. Da dieser keine 'gekürzte' Version kennt, 
# arbeiten wir für einen SYRIUS Master immer mit den 'vollen' die API-Bridge-Versionen (a.b.c-ALPHA-xyz). Bei allen anderen Releasen mit der Kurz-Version
# Anstelle eines simplen Strings könnte evtl. auch direkt eine Map erstellt werden. Das wäre performance technisch vielleicht noch schneller
function getSyrReleases2APIBridgeVersionMapping4APIBridgeBranchName(){
  apiBridgeBranchName=$(getShortedAPIBridgeBranchName $1)
  syrReleases2APIBridgeVersion=''  
  while read line; do		
    shortAPIBridgeVersionFromFile=$(getShortedAPIBridgeVersion $line) # Die Kurze Version auslesen, damit wir sie mit dem Branch vergleichen können
    fullAPIBridgeVersionFromFile=$(getAPIBridgeVersionFromLine $line)
    if [[ "$apiBridgeBranchName" = "$fullAPIBridgeVersionFromFile" ]] || [ "$apiBridgeBranchName" = "$shortAPIBridgeVersionFromFile" ]; then
      syrReleases2APIBridgeVersion=$fullAPIBridgeVersionFromFile';'$(getSyrReleaseFromLine $line)' '$syrReleases2APIBridgeVersion
    fi
  done < $apiBridge2SyrReleaseFile
  echo $syrReleases2APIBridgeVersion | sed -r 's/[ ]$//g' 				# Der letzte Abstand zum Schluss wieder wegmachen
}

# Extrahieren des XSD-File namens: Zuerst die Anzahl an / zählen, damit bekannt ist, wo der Filename beginnt. 
# Dann mittels substring den Pfad abschneiden +1 da der Substring vom letzten Vorkommen '/' aus geht.
function getSimpleFileName(){
  file=$1
  amountOfSlashes=$(($(tr -dc '/' <<<$file | awk '{ print length; }')+1))
  echo $file | cut -d '/' -f $amountOfSlashes | sed 's/.xsd//'
}

# Auslesen der API-Version via git show, um den kompletten Inhalt vom xsd anzuzeigen 
# -> greppen nach dem Versions-Tag und nur dieses behalten (-oP) (Ergebnis: 'version="1.0.0' 
# -> Mit 'sed -r' und Regex den Buchstaben-Teil vom String replacen und so die reine Version extrahieren -> '1.0.0'.
# -> Replace: Mit dem Zusatz -r kann irgen ein Pattern mit einem anderen String ersetzt werden -> 'sed -r s/(pattern)/ersatz/'. 
#	 Ohne -r kann anstelle von (pattern) direkt der zu ersetzende Wert eingetragen werden
function readAPIBridgeServiceVersion(){
  file=$1
  branch=$2
  apiBridgeVersionTagPrefix='version="'	
  apiBridgeVersionPattern=$apiBridgeVersionTagPrefix'([0-9]+)[.]([0-9]+)[.]([0-9]+)'
  git show $branch':'$file | grep -oP $apiBridgeVersionPattern | sed -r 's/('$apiBridgeVersionTagPrefix')//'
}

# Auslesen des reinen Branchnamen, ohne das Präfix 'refs/remotes/origin/' davor. Das würden wir gerne weg machen
# Anhand diesem gekürzten Branchnamen (z.B. 3.1) können aus dem Mapping-File alle SYR-Releases ermittelt werden, welche auf eine 
# solche API-Bridge-Version zeigen (also z.B. werden die Releases rel3_xx_HEAD & rel3_xx_03 ermittelt, welche auf die 3.1.1 bzw. 3.1.0 zeigen

# Sonderfall ist wieder einmal der API-Master-Branch -> der heisst ausserhalb von git nicht master, sondern a.b.c-ALPHA-xy. 
# Darum soll anhand des Mapping-File diese a.b.c-ALPHA ermittelt werden
function getShortedAPIBridgeBranchName(){
  branch=$1
  if [[ "$branch" == "refs/remotes/origin/master" ]]; then
    echo $(getAPIBridgeVersion4SyrRelease 'master')
  else 
    echo $branch | cut -d '/' -f 5
  fi
}

# Ermittelt für einen übergebenen Syrius-Release die entsprechende Version der API-Bridge. 
# Wurde im Versions-Mapping file der entsprechende SYRIUS-Release gefunden, wird dessen API-Brdige version ausgelesen (voll/unkgekürzt)
function getAPIBridgeVersion4SyrRelease(){  
  apiBridgeVersionFromFile=
  syrReleaseIn=$1
  while read line; do	
    syrRelease=$(getSyrReleaseFromLine $line)
    if [[ "$syrReleaseIn" == "$syrRelease" ]]; then
      apiBridgeVersionFromFile=$(getAPIBridgeVersionFromLine $line)
      break
    fi
  done < $apiBridge2SyrReleaseFile
  echo $apiBridgeVersionFromFile
}

# Ermittelt ein Regex-Pattern, anhand dem die relevanten API-Bridge Branches definiert werden
# Zuerst die API-Bridge Version aus dem Mapping-File mit dem SYRIUS-Release rausschneiden
# Dann die Zahl nach dem lezten . abschneiden und ein 'release/' vorne dran hängen : 3.1.1 -> 'release/'3.1
# Schlussendlich alles zu einem String in der Form /1.0|/2.1|etc. concatinieren & noch 'master' dranhängen.
# Das Problem mit den gekürzten Versionen ist, dass z.B. für ein 3.1 auch der 3.1-RC erwischt wird, auch wenn für diesen gar keine SYRIUS-Integration
# existiert. Darum müssen diese Branches in der 'readAllAPIBridgeVersionsAndWrite2File()' übersprungen werden
function getRelevantAPIBranchesNamePattern(){
  relevantReleases=''
  while read line; do	
    apiBridgeVersion='release/'$(getShortedAPIBridgeVersion $line)
    relevantReleases=$apiBridgeVersion' '$relevantReleases
  done < $apiBridge2SyrReleaseFile
  relevantReleases=$(echo $relevantReleases| xargs -n1 | sort -u | xargs)	# Sortieren & Duplikate entfernen (geht irgendwie nur, wenn die Elemente mit Leerzeichen verbunden sind)
  echo $relevantReleases'|master' | sed -r 's/[ ]/|/g' 				# Die Leerzeichen wieder durch ein | ersetzen
}

# Die Branchnamen der API-Bridge sind abweichend zu den API-Bridge versionen. Z.B gibt es eine Version 4.1.2, der entsprechende Branch heisst allerdings 4.1
# Oder 4.1.0-RC-12 und der Branch heisst 4.1-RC
function getShortedAPIBridgeVersion(){
  apiBridgeVersion=$(getAPIBridgeVersionFromLine $1)
  apiBridgeVersion=$(echo $apiBridgeVersion | sed -r 's/([.][0-9]+$)//g') 		# Die Zahl & der Punkt ganz zum Schluss abschneiden. Damit wird aus 4.1.2 -> 4.1
  apiBridgeVersion=$(echo $apiBridgeVersion | sed -r 's/(([.][0-9][-]))/-/g') 		# Oder aus 4.1.1-RC-2 -> 4.1-RC-2
  apiBridgeVersion=$(echo $apiBridgeVersion | sed -r 's/(([-][0-9]+))//g') 		# Und schlussendlich aus 4.1-RC-2 -> 4.1-RC
  echo $apiBridgeVersion
}

# Schneidet die API-BridgeVersion aus dem String 'a.b.c-RC-1;rel3_xx_HEAD' heraus
function getAPIBridgeVersionFromLine(){
  echo $1 | cut -d ';' -f 1
}

# Schneidet den SYRIUS-Release aus dem String 'a.b.c-RC-1;rel3_xx_HEAD' heraus
function getSyrReleaseFromLine(){
  echo $1 | cut -d ';' -f 2
}

if [ -n "$3" ]
then
  cd $3 # Falls das Skript ausserhalb von einem Git-Repo aufgerufen wird, ist ein cd notwendig. Andernfalls können keine git-Befehle abgesetzt werden
fi

createEmptyAPIBridgeVersionFile
readAllAPIBridgeVersionsAndWrite2File
