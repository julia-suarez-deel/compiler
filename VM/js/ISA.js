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

function UJP(address){
    console.log(address-1);
    PC=address-1;
}

function FJP(address){
    let value = parseInt(stack.pop().value);   
    SP--;
    if (value==0) {
        console.log(address-1);
        PC=address-1;
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
        $("#alert-container").append("<div class='alert alert-danger alert-dismissible' role='alert'>"+ 
                                    "<button type='button' class='close' data-dismiss='alert' aria-label='Close'>"+
                                    "<span aria-hidden='true'>&times;</span></button>"+
                                    "<strong>Error de ejecución</strong>"+
                                    "</div>");
        
    }
}
function ENT(address){
    console.log(address);
    let value1;
    while ((SP - MP) > 0) {
        value1 = parseInt(stack.pop().value);
        data[address.value + (SP - MP)] = value1;
        SP--;
    }
}
function MST(){
    MP = SP;
    console.log(SP);
}
function CUP(line){
    console.log(line);
    console.log(PC);
    PC = line;
    console.log(line);
    console.log(PC);
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
    data[address] = new DataLine(address,value);
}