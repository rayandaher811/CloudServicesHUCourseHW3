const EventSource = require('eventsource');
const { Kafka } = require("kafkajs")

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

// Wiki event streams registeration
const eventSource = new EventSource('https://stream.wikimedia.org/v2/stream/recentchange');
const revertEventSource = new EventSource('https://stream.wikimedia.org/v2/stream/mediawiki.revision-tags-change');

eventSource.onopen = () => {
    console.info('Opened connection.');
};
revertEventSource.onopen = () => {
    console.info('Opened connection.');
};

eventSource.onerror = (event) => {
    console.error('Encountered error', event);
};
revertEventSource.onerror = (event) => {
    console.error('Encountered error', event);
};

// Setting event's tunnel open times to handle the load properly
let receivedEventsCountInterval = 0
let receivedRevertEventsCountInterval = 0

setInterval(() => {
    if (receivedEventsCountInterval >= 3) {
        receivedEventsCountInterval = 0
    }

    if (receivedRevertEventsCountInterval >= 3) {
        receivedRevertEventsCountInterval = 0
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
};

revertEventSource.onmessage = (event) => {
    if (receivedRevertEventsCountInterval >= 3) {
        return
    }
    receivedRevertEventsCountInterval++;

    const data = JSON.parse(event.data)

    if (data.tags.includes("mw-reverted")) {
        produceMessage(pageEventTopic, data.meta.domain, data.performer.user_text, data.performer.user_is_bot, data.meta.uri)
        produceMessage(pageRevetActionTopic, data.meta.domain, data.performer.user_text, data.performer.user_is_bot, data.meta.uri)
    }

};

function produceMessage(topic, domain, user, bot, uri) {
    try {
        var language = domain.split(".")[0]

        // Detecting the language
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