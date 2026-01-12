
var readline = require('readline');
var rl = readline.createInterface({
    input: process.stdin,
    output: process.stdout
 });

rl.on('line', function(line){
    console.log(parseInt(line.replace(/#/g, "1").replace(/\./g, "0"), 2))
})
