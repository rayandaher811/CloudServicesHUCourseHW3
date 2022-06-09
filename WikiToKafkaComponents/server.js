const createHookReceiver = require('npm-hook-receiver')

const main = async () => {
  const server = createHookReceiver({
    // Secret created when registering the webhook with NPM.
    // Used to validate the payload. 
    secret: "Hello there",

    // Path for the handler to be mounted on.
    mount: '/hook'
  })

  server.on('package:publish', async event => {
    // Send message to Kafka
  })

  server.listen(process.env.PORT || 3000, () => {
    console.log(`Server listening on port ${process.env.PORT || 3000}`)
  })
}

main().catch(error => {
  console.error(error)
  process.exit(1)
})