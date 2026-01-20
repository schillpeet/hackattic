
var readline = require('readline');
var rl = readline.createInterface({
    input: process.stdin,
    output: process.stdout
 });

rl.on('line', function(line){
    const date = new Date(Number(line) * 86400 * 1000)
    const result = date.toLocaleDateString('en-US', { weekday: 'long' })
    console.log(result)
});
