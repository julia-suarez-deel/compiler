const CODE_URL = 'http://localhost:63342/compiler/VM/code.txt';
const ISA = {
    'instruction1':instruction1,
    'instruction2':instruction2,
    'instruction3':instruction3,
};
$(document).ready(function(){
    readCode();
});
function readCode(){
    $.get(CODE_URL, function(data) {
        let instructions = splitInstructions(data);
        instructions.forEach(line => {
            try{
                let instruction = splitArguments(line);
                instruction.function = ISA[instruction.name];
                if(instruction.function.length === instruction.args.length){
                    instruction.function.apply(this, instruction.args);
                }
                else{
                    throw new Error("Number of arguments doesn't match.")
                }
            }catch (e) {
                console.log(e.message);
            }
        });
     }, 'text');
    console.log('end');
}
function splitInstructions(data){
    let clean_data = data.replace(/[\r\n]+/g,'');
    let instructions = clean_data.split(';');
    let instructions_not_empty = instructions.filter( x => x);
    return instructions_not_empty;
}
function splitArguments(data) {
    let instruction={};
    instruction.name = data.split(" ")[0];
    instruction.args = data.split(" ");
    delete instruction.args[0];
    return instruction;
}
function instruction1(self,a,b,c){
    console.log('instruction1');
}
function instruction2(a,b,c){
    console.log('instruction2');
}
function instruction3(b,c){
    console.log('instruction3');
}