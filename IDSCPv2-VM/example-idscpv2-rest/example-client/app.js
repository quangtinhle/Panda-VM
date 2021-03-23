
const http = require('http')
const options = {
  hostname: 'provider-core',
  port: 8880,
  path: '/data',
  method: 'POST',
  headers: {
    'Content-Type': 'application/json'
  },
}
setInterval(() => {
  json = {
    patientId: String(Math.random()) * 10,
    repositoryId: String(Math.random()* 10)
}

const data = JSON.stringify(json)

const req = http.request(options, (res) => {
  console.log(` ${data}statusCode: ${res.statusCode}`)

  res.on('data', (d) => {
    process.stdout.write(d)
  })
})

req.on('error', (error) => {
  console.error(error)
})

req.write(data)
req.end()

}, 15000)
