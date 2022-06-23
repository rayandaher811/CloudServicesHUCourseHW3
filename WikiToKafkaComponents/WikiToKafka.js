//////////////////////////////////////////////
// Delete me later
const consume = require("./kafka-test/consume")
// start the consumer, and log any errors
consume("user-activities-language-count").catch((err) => {
    console.error("error in consumer: ", err)
})
//////////////////////////////////////////////

const EventSource = require('eventsource');
const { Kafka } = require("kafkajs")

// import the `Kafka` instance from the kafkajs library

// the client ID lets kafka know who's producing the messages
const clientId = "my-app"

// we can define the list of brokers in the cluster
const brokers = ["localhost:9092"]

// All my topic names
const pageCreationTopic = "page-creation"
const pageEventTopic = "page-event"
const pageUpdateTopic = "page-update"
const pageRevetActionTopic = "page-revert-action"

kafkaInstance = new Kafka({ clientId, brokers })

producer = kafkaInstance.producer()
producer.connect()

function produceMessage(topic, domain, user, bot, uri) {
    try {
        var language = domain.split(".")[0]
        if(language === "commons" || language === "www" || language === "meta")
            language = "common"
        // send a message to the inserted topic with
        producer.send({
            topic,
            messages: [
                {
                    value: JSON.stringify({ 
                        "user": user,
                        "bot": bot,
                        "language": language,
                        "uri": uri
                    })
                },
            ],
        })

        // if the message is written successfully
        console.log(`A message had been written from server ${domain} and uri ${uri}`)
    } catch (err) {
        console.error("could not write message " + err)
    }
}


// Wiki event stream registeration
const eventSource = new EventSource('https://stream.wikimedia.org/v2/stream/recentchange');

eventSource.onopen = () => {
    console.info('Opened connection.');
};

eventSource.onerror = (event) => {
    console.error('Encountered error', event);
};

// Setting event's tunnel open times to handle the load properly
let receivedEventsCountInterval = 0
setInterval(() => {
    if (receivedEventsCountInterval >= 3) {
        receivedEventsCountInterval = 0
    }
}, 1000)

eventSource.onmessage = (event) => {
    if (receivedEventsCountInterval >= 3) {
        return
    }
    receivedEventsCountInterval++;

    const data = JSON.parse(event.data)

    if (data.type === 'edit') {
        produceMessage(pageEventTopic, data.meta.domain, data.user, data.bot, data.meta.uri)
        produceMessage(pageUpdateTopic, data.meta.domain, data.user, data.bot, data.meta.uri)
    }
    else if (data.type === 'new') {
        produceMessage(pageEventTopic, data.meta.domain, data.user, data.bot, data.meta.uri)
        produceMessage(pageCreationTopic, data.meta.domain, data.user, data.bot, data.meta.uri)
    }
    else if (data.type === 'revert') {
        produceMessage(pageEventTopic, data.meta.domain, data.user, data.bot, data.meta.uri)
        produceMessage(pageRevetActionTopic, data.meta.domain, data.user, data.bot, data.meta.uri)
    }

};
