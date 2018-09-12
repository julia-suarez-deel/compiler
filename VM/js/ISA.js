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
    let address = stack.pop().value;
    let value = stack.pop().value;
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

function UJP(address){
    console.log(address-2);
    PC=address-2;
}

function FJP(address){
    let value = parseInt(stack.pop().value);   
    if (value==0) {
        console.log(address-2);
        PC=address-2;
    }   
}

function EQU(){
    let value1 = parseInt(stack.pop().value);
    let value2 = parseInt(stack.pop().value);
    SP-=2;
    if (value2==value1) {
        stack.push(new StackLine(1));
    }else{
        stack.push(new StackLine(0));
    }   
}

function GRT(){
    let value1 = parseInt(stack.pop().value);
    let value2 = parseInt(stack.pop().value);
    SP-=2;
    if (value2>value1) {
        stack.push(new StackLine(1));
    }else{
        stack.push(new StackLine(0));
    } 
}

function STP(){

}
function ADI(){
    let value1 = parseInt(stack.pop().value);
    let value2 = parseInt(stack.pop().value);
    SP-=2;
    stack.push(new StackLine(value1+value2));
}
function SBI(){
    let value1 = parseInt(stack.pop().value);
    let value2 = parseInt(stack.pop().value);
    SP-=2;
    stack.push(new StackLine(value1-value2));
}
function MPI(){
    let value1 = parseInt(stack.pop().value);
    let value2 = parseInt(stack.pop().value);
    SP-=2;
    stack.push(new StackLine(value1*value2));
}
function DVI(){
    let value1 = parseInt(stack.pop().value);
    let value2 = parseInt(stack.pop().value);
    SP-=2;
    stack.push(new StackLine(value1/value2));
}
function LAB(address){
    console.log("----Etiqueta-- "+address);
}
