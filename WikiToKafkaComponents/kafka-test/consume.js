// import the `Kafka` instance from the kafkajs library
const { Kafka } = require("kafkajs")

// the client ID lets kafka know who's producing the messages
const clientId = "consumer-app"
// we can define the list of brokers in the cluster
const brokers = ["localhost:9092"]
// this is the topic to which we want to write messages
const topic = "page-revert-action"

// initialize a new kafka client and initialize a producer from it
const kafka = new Kafka({ clientId, brokers })

// create a new consumer from the kafka client, and set its group ID
// the group ID helps Kafka keep track of the messages that this client
// is yet to receive
const consumer = kafka.consumer({ groupId: clientId })

const consume = async () => {
	// first, we wait for the client to connect and subscribe to the given topic
	await consumer.connect()
	await consumer.subscribe({ topic })
	await consumer.run({
		// this function is called every time the consumer gets a new message
		eachMessage: ({ message }) => {
			try {// here, we just log the message to the standard output
				//let i = JSON.parse(message.value)
				console.log(`received message: key: ${message.key.toString()} , value: ${message.value.readBigInt64BE().toString()}`)
				//let i = parse(message.value)
				//console.log(`received message: ${i.user}`)
			}
			catch (e) {
				console.log(`received message with error`)
			}
		},
	})
}

module.exports = consume