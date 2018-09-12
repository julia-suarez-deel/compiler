function LDA(address){
    stack.push(new StackLine(address));
}
function LOD(address){
    stack.push(new StackLine(data[address].value));
}
function LDC(constant){
    stack.push(new StackLine(constant));
}
function STO(){
    let value = stack.pop().value;
    let address = stack.pop().value;
    data[address] = new DataLine(address,value);
    SP-=2;
}
function STN(){
    let value = stack.pop().value;
    let address = stack.pop().value;
    console.log(address);
    data[address] = new DataLine(address,value);
    SP-=2;
    stack.push(new StackLine(value));
}
function IXA(factor){
    // TODO: Change the compiler so the factor would be a number and not elem_size
    factor = 1;
    let address = stack.pop().value;
    let delta = stack.pop().value;
    stack.push(new StackLine(address+delta*factor));
}
function IND(delta){
    let address = stack.pop().value;
    SP--;
    let data_value = data[address + delta].value;
    stack.push(new StackLine(data_value));
}
function UCJ(address){

}
function EQU(){

}
function GEQ(){

}
function STP(){
    haltProgram();
}
function ADI(){
    let value1 = parseInt(stack.pop().value);
    let value2 = parseInt(stack.pop().value);
    SP-=2;
    stack.push(new StackLine(value2+value1));
}
function SBI(){
    let value1 = parseInt(stack.pop().value);
    let value2 = parseInt(stack.pop().value);
    console.log(value2-value1);
    SP-=2;
    stack.push(new StackLine(value2-value1));
}
function MPI(){
    let value1 = parseInt(stack.pop().value);
    let value2 = parseInt(stack.pop().value);
    SP-=2;
    stack.push(new StackLine(value2*value1));
}
function DVI(){
    let value1 = parseInt(stack.pop().value);
    let value2 = parseInt(stack.pop().value);
    SP-=2;
    stack.push(new StackLine(value2/value1));
}
function LAB(address){

}
