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
    let address = parseInt(stack.pop().value);
    data[address] = new DataLine(address,value);
    SP-=2;
}
function STN(){
    let value = parseInt(stack.pop().value);
    let address = parseInt(stack.pop().value);
    console.log(address);
    data[address] = new DataLine(address,value);
    SP-=2;
    stack.push(new StackLine(value));
}
function IXA(factor){
    let delta = parseInt(stack.pop().value);
    let address = parseInt(stack.pop().value);
    SP-=2;
    stack.push(new StackLine(address + factor * delta));
}

function IND(delta){
    let address = parseInt(stack.pop().value);
    delta = parseInt(delta);
    SP--;
    let data_value = data[address + delta].value;
    stack.push(new StackLine(data_value));
}

function UJP(line){
    console.log(line-1);
    PC=line-1;
}

function FJP(line){
    let value = parseInt(stack.pop().value);   
    SP--;
    if (value==0) {
        console.log(line-1);
        PC=line-1;
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
    if(value1 != 0){
        let result = parseInt(value2/value1);
        stack.push(new StackLine(result));
    }
    else{
        haltProgram();
        executionErrorMessage();
    }
}
function ENT(address){
    //Se guarda el tope (la instruccion a donde retornará)
    let value1;
    let valueTemp = parseInt(stack.pop().value);
    SP--;
    //Se cargan los argumentos en orden hasta el marcador de pila (MP)
    while ((SP - MP) > 0) {
        addr = parseInt(address) + (SP - MP);
        value1 = parseInt(stack.pop().value);
        SP--;
        data[addr] = new DataLine(addr, value1);
    }
    //Se coloca la instrucción de retorno en el tope de la pila
    stack.push(new StackLine(valueTemp));
}
function MST(){
    //Se marca el comienzo de la pila para la funcion.
    MP = SP;
}
function CUP(line){
    //Se introduce la instruccion actual como instrucción de retorno
    stack.push(new StackLine(PC));
    //Se hace el salto a la función.
    PC = line-1;
}
function RET(){
    //Se guarda el tope de la pila (valor de retorno)
    let valueTemp = parseInt(stack.pop().value);
    SP--;
    //Se hace el salto a la instruccion de retorno.
    PC = parseInt(stack.pop().value);
    SP--;
    MP=0;
    //Se coloca el valor de retorno en el tope de la pila.
    stack.push(new StackLine(valueTemp));
}
function LAB(address){
    console.log("----Etiqueta-- "+address);
}
function WRI(){
    let value = parseInt(stack.pop().value);
    SP--;
    $('#console-body').append('Valor en tope de la pila: '+value+'<br>>&nbsp;');
}
function RDI(){
    let value = prompt('Indica el valor a leer: ');
    let address = parseInt(stack.pop().value);
    SP--;
    data[address] = new DataLine(address,value);
}