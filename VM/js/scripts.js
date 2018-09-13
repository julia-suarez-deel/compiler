// Variables
const ISA = {
    'LDA':LDA,
    'LOD':LOD,
    'LDC':LDC,
    'STO':STO,
    'STN':STN,
    'IXA':IXA,
    'IND':IND,
    'UJP':UJP,
    'FJP':FJP,
    'EQU':EQU,
    'GRT':GRT,
    'STP':STP,
    'ADI':ADI,
    'SBI':SBI,
    'MPI':MPI,
    'DVI':DVI,
    'LAB':LAB,
    'ENT':ENT,
    'MST':MST,
    'CUP':CUP,
    'RDI':RDI,
    'WRI':WRI,
    'RET':RET
};
const DATA_SIZE = 10;
const SUCCESS_ROW_CLASS = 'bg-success text-light';
const STACK_CONTAINER_SELECTOR = '#stack-container tbody';
const DATA_CONTAINER_SELECTOR = '#data-container tbody';
let is_executing = false;
let PC = 0;
let SP = 0;
let MP = 0;
let $templates = null;
let instructions = [];
let stack = [];
let data = Array(DATA_SIZE).fill({});
let toolbar = null;
// Helper classes
class Toolbar {
    constructor() {
        this.$node = null;
        this.unique_selector = '#toolbar';
        this.template_selector = '#toolbar';
        this.parent_selector = '#toolbar-container';
        this.start_or_next = { icon:'play' };
        this.stop = this.redo = { state: 'disabled' };
        createOrUpdateDomNode(this, true);
        this.setupEvents();
    }
    updateState(){
        if(is_executing){
            this.start_or_next.icon = 'arrow-right';
            this.stop.state = this.redo.state = '';
        }
        createOrUpdateDomNode(this);
        this.setupEvents();
    }
    setupEvents(){
        $('#start_or_next.btn').on('click', function () {
            let length = $( ".alert-danger" ).length;
            if(length > 0)
                $(".alert-danger").remove();
            
            if(PC+1<=instructions.length){
                is_executing = true;
                toolbar.updateState();
                console.log("---------- "+PC);
                instructions[PC].execute();
            }
            else{
                haltProgram();
            }
        });
        $('#stop.btn').on('click', function () {
            if(is_executing){
                is_executing = false;
                PC = 0;
                toolbar.updateState();
                haltProgram();
                autoScrolling();
            }
        });
        $('#redo.btn').on('click', function () {
            if(is_executing){
                haltProgram();
                instructions[PC].execute();
            }
        })
    }
}
class Instruction{
    constructor(line, i){
        let tokens = line.split(" ");
        let instruction_name = tokens[0];
        tokens.splice(0, 1);
        this.parent_selector = '#instructions-container';
        this.template_selector ='.instruction';
        this.unique_selector = '.instruction[number='+i+']';
        this.name =  instruction_name;
        this.args = tokens;
        this.line = line;
        this.number = i;
        this.function = ISA[instruction_name];
        this.$node = null;
        for (let i = 0; i < this.args.length; i++) {
            this.args[i] = parseInt(this.args[i]);
        }
    }
    execute(){
        $('.instruction').removeClass(SUCCESS_ROW_CLASS);
        try{
            if(this.function.length === this.args.length){
                this.$node.addClass(SUCCESS_ROW_CLASS);
                PC++;
                this.function.apply(this, this.args);
                loadHtmlArray(stack,STACK_CONTAINER_SELECTOR);
                loadHtmlArray(data,DATA_CONTAINER_SELECTOR);
            }
            else{
                throw new Error("Number of arguments doesn't match.")
            }
        }catch (e) {
            console.log(e.message);
        }
        autoScrolling();
    }
}
class StackLine {
    constructor(value){
        this.value = value;
        this.number = SP;
        SP++;
    }
}
class DataLine{
    constructor(address, value){
        this.address = address;
        this.value = value;
    }
}
// Setup
$(document).ready(function(){
    loadTemplates();
    readCode();
    toolbar = new Toolbar();
    loadHtmlArray(data, DATA_CONTAINER_SELECTOR);
});
// Helper functions
function loadTemplates(){
    $.ajax({
        url:TEMPLATES_URL,
        async:false,
        complete:function(response) {
            let clean_template = cleanLine(response.responseText);
            $templates = $(clean_template);
        }
    });
}
function readCode(){
    $.get(CODE_URL, function(program) {
        let program_lines = splitInstructions(program);
        let i=1;
        program_lines.forEach(line => {
            let instruction = new Instruction(line, i);
            instructions.push(instruction);
            createOrUpdateDomNode(instruction, true);
            i++;
        });
     }, 'text');
}
function splitInstructions(data){
    let clean_data = cleanLine(data);
    let instructions = clean_data.split('\n');
    let instructions_not_empty = instructions.filter( x => x);
    return instructions_not_empty;
}
function createOrUpdateDomNode(object, create=false){
    let template_html = $templates.find(object.template_selector)[0].outerHTML;
    let rendered_html = Mustache.render(template_html, object);
    if(create){
        $(object.parent_selector).append(rendered_html);
        object.$node = $(object.unique_selector);
    }
    else{
        $(object.unique_selector).html(rendered_html);
    }
}
function loadHtmlArray(array, parent_selector){
    let complete_html = "";
    let i = 0;
    array.forEach(item => {
        item.number = i;
        let template_html = $templates.find('.line')[0].outerHTML;
        let rendered_html = Mustache.render(template_html, item);
        complete_html+=rendered_html;
        i++;
    });
    $(parent_selector).html(complete_html);
}
function haltProgram() {
    PC = SP = 0;
    $('.instruction').removeClass(SUCCESS_ROW_CLASS);
    $('#toolbar').remove();
    $('#console-body').empty();
    $('#console-body').append('>&nbsp;');
    toolbar = new Toolbar();
    stack = [];
    data = Array(DATA_SIZE).fill({});
    loadHtmlArray(stack,STACK_CONTAINER_SELECTOR);
    loadHtmlArray(data, DATA_CONTAINER_SELECTOR);
}
function autoScrolling(){
    let consol = document.getElementById('console');
    consol.scrollTop = consol.scrollHeight;
    let instruction = document.getElementById('body-instructions');
    instruction.scrollTop = (instruction.scrollHeight/instructions.length)*(PC-1);
    let stack_table = document.getElementById('body-stack');
    stack_table.scrollTop = stack_table.scrollHeight;
} 