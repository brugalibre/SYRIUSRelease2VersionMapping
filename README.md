Dieses Progrämmchen erstellt eine Excel-Datei in welcher das Mapping zwischen SYRIUS-Releasen und der Version der verschiedenen BPMN-Prozessen aufgelistet sind.
Die dazu benötigten Rohdaten dazu werden mittels Bash-Skript ermittelt und in eine CSV-Datei Datei geschrieben. 
Aus convenience Gründen wurde dieses Bash-Skript in die Jar-Datei integriert, dadurch kann das Excel durch das Starten einer Java-Datei erstellt werden

Damit erübrigt sich der Download von zwei Dateien, wobei die Shell-Datei jeweils mittels chmod +x lauffähig gemacht werden musste. Der Nachteil ist ein etwas komplizierteres
Java-Progrämmchen, da dieses das Bash-Skript auf das Filesystem kopiert und ausführt.

Der Ablauf des Programms ist etwa so:
- Start des Java-Programms
  - java -jar BPMNProcessVersionMapping.jar (wenn es _innerhalb_ eines git repos aufgerufen wird)
  - java -jar BPMNProcessVersionMapping.jar rel3_10_HEAD/syrius (wenn es _ausserhalb_ eines git repos aufgerufen wird)
- Kopieren des Bash-Skripts, welches innherlab dieses Java-Programms liegt, auf das Filesystem (selbe Ordnerebene wie die .jar Datei)
- Ausführen dieses Bash-Skripts
  - Dadurch wird eine csv-Datei mit den Rohdaten im Home-Verzeichnis angelegt
- Erstellen des Excels
  - Anhand der Rohdaten in der csv-Datei wird mittels der Appache-Poi-Library im Home-Verzeichnis die Datei 'BPMN-Versionierung-Bestand.xls' angelegt 
