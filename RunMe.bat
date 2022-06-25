: Installing the wiki to kafka dependecies
CALL cd WikiToKafkaComponents
CALL npm install
CALL cd ..

: Running the whole kafka cluster and waiting till all topics has been created
CALL docker compose up -d
CALL echo please wait:
CALL echo Creating all kafka topics please wait 30 seconds:
CALL timeout 30 

: Running the wiki to kafka script via node
CALL start cmd /C node WikiToKafkaComponents\WikiToKafka.js

: Running our kafka streams jar
CALL start cmd /C java -jar "_______________________"

: Running our topic listener jar
CALL start cmd /C java -jar "_______________________"
