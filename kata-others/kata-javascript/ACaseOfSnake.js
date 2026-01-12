
var readline = require('readline');
var rl = readline.createInterface({
    input: process.stdin,
    output: process.stdout
 });

rl.on('line', function(line){
    var words = line.split(/(?=[A-Z])/)
    if (words[0].length <= 3 && words.length > 2) words.shift()
    const rest = words.map(i => i.toLocaleLowerCase()).join('_')
    console.log(rest)
});
