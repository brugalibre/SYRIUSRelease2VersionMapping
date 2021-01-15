Das Progrämmchen 'BPMNProcessVersionMapping' erstellt eine Excel-Datei in welcher das Mapping zwischen SYRIUS-Releasen und der Version der verschiedenen BPMN-Prozessen aufgelistet sind.Die dazu benötigten Rohdaten dazu werden mittels Bash-Skript ermittelt und in eine CSV-Datei Datei geschrieben. 
Aus convenience Gründen wurde dieses Bash-Skript in die Jar-Datei integriert, dadurch kann das Excel durch das Starten einer Java-Datei erstellt werden

Das zweite Programm 'APIBridgeVersionMapping' erstellt eine Excel-Datei, in welcher im einen Blatt das Versionmapping zwischen SYRIUS-Releasen, der API-Bridge-Integration sowie der API-Bridge und den effektiven API-Bridge-Services und in einem weiteren Blatt lediglich das Mapping zwischen der API-Bridge und der API-Bridge-SYRIUS-Integration enthalten sind.

Dank dem Java-Wrapper erübrigt sich der Download von mehreren Bash-Skripts, wobei diese jeweils mittels chmod +x lauffähig gemacht werden mussten. Der Nachteil ist ein etwas komplizierteres Java-Progrämmchen, da dieses das bzw. die Bash-Skripts auf das Filesystem kopiert und ausführt.

Der Ablauf des BPMN-Versionierungs-Programms ist etwa so:
- Start des Java-Programms
  - java -jar BPMNProcessVersionMapping.jar (wenn es _innerhalb_ eines git repos aufgerufen wird)
  - java -jar BPMNProcessVersionMapping.jar rel3_10_HEAD/syrius (wenn es _ausserhalb_ eines git repos aufgerufen wird)
- Kopieren des Bash-Skripts, welches innherlab dieses Java-Programms liegt, auf das Filesystem (selbe Ordnerebene wie die .jar Datei)
- Ausführen dieses Bash-Skripts
  - Dadurch wird eine csv-Datei mit den Rohdaten im Home-Verzeichnis angelegt. Im Anschluss an die Ausführung wird das Skript gelöscht
- Erstellen des Excels
  - Anhand der Rohdaten in der csv-Datei wird mittels der Appache-Poi-Library im Home-Verzeichnis die Datei 'BPMN-Versionierung-Bestand.xls' angelegt 
  - Zum Schluss werden die Rohdaten gelöscht

Der Ablauf des API-Bridge-Versionierungs-Programms ist etwa so:
- Start des Java-Programms
  - java -jar APIBridgeVersionMapping.jar apibridge-syriusintegration-bestandsverw/ apibridge-bestandsverw/
- Kopieren des Bash-Skripts um das Versions-Mapping zwischen SYRIUS und der API-Bridge Integration zu erstellen
- Ausführen dieses Bash-Skripts
  - Dadurch wird eine csv-Datei mit dem Mapping im Home-Verzeichnis angelegt (das Skript wird anschliessend gelöscht)
- Kopieren des Bash-Skripts um die Rohdaten der Versions-Mapping zwischen SYRIUS, der API-Bridge Integration und der API-Bridge zu erstellen
- Ausführen dieses Bash-Skripts
  - Dadurch wird eine csv-Datei mit den Rohdaten im Home-Verzeichnis angelegt (das Skript wird anschliessend gelöscht)
- Erstellen des Excels mit zwei Excel-Blättern
  - Anhand der Rohdaten in der csv-Datei wird mittels der Appache-Poi-Library im Home-Verzeichnis die Datei 'API-Bridge-Versionierung-Bestand.xls' angelegt 
  - Zuletzt werden die beiden temporären csv-Dateien gelöscht
