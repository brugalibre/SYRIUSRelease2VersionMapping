#!/bin/bash

###############################################################################################################
#											Read-me    	     											      #
# Das Skript kann ohne weitere Angabe von Command-Line-Argumenten auf der Konsole gestartet werden, 		  #
# sofern sich der Pfad innerhalb eines Git-Repos befindet. Andernfalls muss der Pfad zu einem git-Repo 		  #
# angegeben werden. Von dort aus können alle relevanten Git-Repos gefetch und nach BPMN-Dateien 			  #
# abgesucht werden.	  																						  #
#													      													  #
# Die Ergebnisse werden anschliessend im angegebenen Pfad in eine .csv-Daei geschrieben.		      # 
# Die zu prüfenden Branches sowie Prozesse lassen sich bei Bedarf konfigurieren. Bei einem Releasewechsel     #
# muss grundsätzlich nichts angepasst werden. Von Zeit zu Zeit müssen evtl. alte Releases ausgetragen werden  #
#													      #
###############################################################################################################

# Variablen für die zu prüfenden Branches und für den Zugriff zum Repo. Je nach dem welche Branches geprüft werden sollen, gilt es hier Anpassungen zu machen
majorPattern='rel3_[1-9][0-9]' 							# Nur Major Releaeses >= rel3_10
relevantBranches='master|'$majorPattern$'_([0-9]+|(HEAD))'			# Minors & Patches wie z.B. rel3_10_03
notRelevantBranches='feature|bug'						# Aber keine feature/bugfix-Branches

# Filter für die erwünschten und unerwünschten BPMN-Files
relevantBPMNPackageNames='produktwechsel|verkaufsproduktwechsel|unfalldeckungchange|franchisechange|vertragchange|hausarztaenderung|militaersistierungerstellen'
notRelevantBPMNPackageNames='partner'

# Name vom File & sein Pfad
bpmnVersionFileCsv=$1

function createEmptyBPMNVersionFile(){
  rm -f $bpmnVersionFileCsv
  echo "Geschäftsvorfall;Prozess Name;BPMN-Version;SYRIUS-Release" >> $bpmnVersionFileCsv
}

# Auslesen der BPMN-Version: via git show den kompletten Inhalt Anzeigen 
# -> greppen nach dem Versions-Tag und nur dieses behalten (-oP) (Ergebnis: 'camunda:versionTag="1.0.0') 
# -> Mit 'sed -r' und Regex den unnötigen Teil vom String replacen und so die reine Version extrahieren -> 1.0.0.
# -> Replace: Mit dem Zusatz -r kann irgen ein Pattern mit einem anderen String ersetzt werden -> 'sed -r s/(pattern)/ersatz/'. 
#    Ohne -r kann anstelle von (pattern) direkt der zu ersetzende Wert eingetragen werden
function readBPMNVersion(){
  file=$1
  branch=$2
  camundaVersionTagPrefix='camunda:versionTag=["]'
  camundaVersionPattern=$camundaVersionTagPrefix'([0-9]+)[.]([0-9]+)[.]([0-9]+)'
  git show $branch':'$file | grep -oP $camundaVersionPattern | sed -r 's/('$camundaVersionTagPrefix')//'
}

# Extrahieren des BPMN-File namens: Zuerst die Anzahl an / zählen, damit bekannt ist, wo der Filename beginnt. 
# Dann mittels substring den Pfad abschneiden +1 da der Substring vom letzten Vorkommen '/' aus geht.
function getSimpleFileName(){
  file=$1
  amountOfSlashes=$(($(tr -dc '/' <<<$file | awk '{ print length; }')+1))
  echo $file | cut -d '/' -f $amountOfSlashes
}

# Den Geschäftsvorfall aus dem Packagename rausschneiden:
# Erster Buchstabe gross -> (${stringWert^}),
# Da es gewisse Überschneidungen geben kann (z.B Verkaufsproduktwechsel mit Produktwechsel), soll mittels '|head -1' nur der erste vollständige match behalten werden
# Umlaute ersetzen und change zu ändern -> sed 's/ae/ä/g'. Mit dem Zusatz 'g' (global) werden alle Vorkommen ersetzt.
# Sonderfälle: Vertragsparam-Attr ändern & Vertrag ändern aufgrund Adresseänderung. Hier sind die Packagenamen nicht sauber
# Das Ganze könnte sicherlich schöner gemacht werden..
function getGeschaeftsvorfall(){
  file=$1
  geschaeftsvorfall=$(echo $file | grep -oP $relevantBPMNPackageNames | head -1)
  geschaeftsvorfall=${geschaeftsvorfall^}
  echo $geschaeftsvorfall | sed 's/ae/ä/g' | sed 's/change/ändern/' | sed 's/Vertragändern/Vertragändern aufgrund Adressänderung/'
}

# Auslesen des reinen Branchnamen. Dem branchname steht jeweils ein 'refs/remotes/origin/' davor, das würden wir gerne weg machen
function getSyriusRel(){
  branch=$1
  echo $branch | cut -d '/' -f 4
}

# Fetcht die gewünschten Git-Repositories und sucht in diesen nach BPMN-Files, dessen package-namen dem gewünschten pattern entsprechen
# Aus allen Treffern wird dann die BPMN-Version, der eigentliche Name des BPMN-Files, der Geschäftsvorfall und der Syr-Release ermittelt und in die csv-Datei geschrieben
function readAllBPMNVersionsAndWrite2File(){
  for branch in $(git for-each-ref --format="%(refname)" refs/remotes | grep -iE $relevantBranches | grep -vE $notRelevantBranches); do
    for file in $(git ls-tree -r --name-only $branch | grep '.bpmn' | grep -iE $relevantBPMNPackageNames | grep -vE $notRelevantBPMNPackageNames); do
    bpmnVersion=$(readBPMNVersion $file $branch)
    fileName=$(getSimpleFileName $file)
    geschaeftsvorfall=$(getGeschaeftsvorfall $file)
    syriusRel=$(getSyriusRel $branch)
    
    # Ab ins File damit
    echo $geschaeftsvorfall';'$fileName';'$bpmnVersion';'$syriusRel >> $bpmnVersionFileCsv
    done
  done
}
if [ -n "$2" ]
then
  cd $2 # Falls das Skript ausserhalb von einem Git-Repo aufgerufen wird, ist ein cd notwendig. Andernfalls können keine git-Befehle abgesetzt werden
fi
createEmptyBPMNVersionFile
readAllBPMNVersionsAndWrite2File
