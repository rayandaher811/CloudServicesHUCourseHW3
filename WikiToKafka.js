//////////////////////////////////////////////
// Delete me later
const consume = require("./kafka-test/consume")
// start the consumer, and log any errors
consume().catch((err) => {
    console.error("error in consumer: ", err)
})
//////////////////////////////////////////////

const EventSource = require('eventsource');
const { Kafka } = require("kafkajs")

// Wiki event stream registeration
const url = 'https://stream.wikimedia.org/v2/stream/recentchange';
const eventSource = new EventSource(url);

// import the `Kafka` instance from the kafkajs library

// the client ID lets kafka know who's producing the messages
const clientId = "my-app"

// we can define the list of brokers in the cluster
const brokers = ["localhost:9092"]

// All my topic names
const pageCreationTopic = "page-creation"
const pageUpdateTopic = "page-update"
const pageRevetActionTopic = "page-revert-action"

kafkaInstance = new Kafka({ clientId, brokers })

producer = kafkaInstance.producer()
producer.connect()

function produceMessage(topic, eventData) {
    try {
        // send a message to the inserted topic with
        producer.send({
            topic,
            messages: [
                {
                    value: JSON.stringify({ "user": eventData.user, "is-bot": eventData.bot, "language": eventData.server_name })
                },
            ],
        })

        // if the message is written successfully
        console.log(`A message had been written from server ${eventData.server_name}`)
    } catch (err) {
        console.error("could not write message " + err)
    }
}

eventSource.onopen = () => {
    console.info('Opened connection.');
};

eventSource.onerror = (event) => {
    console.error('Encountered error', event);
};

// Setting event's tunnel open times to handle the load properly
tunnelOpen = true
let receivedEventsCountInterval = 0
setInterval(() => {
    if (receivedEventsCountInterval >= 10) {
        tunnelOpen = false
        receivedEventsCountInterval = 0
    }
    else
        tunnelOpen = true
}, 500)

eventSource.onmessage = (event) => {
    if (!tunnelOpen) {
        return
    }
    receivedEventsCountInterval++;

    const data = JSON.parse(event.data);
    if (data.type === 'edit') {
        produceMessage(pageUpdateTopic, data)
    }
    else if (data.type === 'new') {
        produceMessage(pageCreationTopic, data)
    }
    else if (data.type === 'revert') {
        produceMessage(pageRevetActionTopic, data)
    }

};
