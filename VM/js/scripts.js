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
const DATA_SIZE = 1024;
const STACK_SIZE = 1024;
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
        let total_function_args = this.function.length
        try{
            if(total_function_args === this.args.length){
                this.$node.addClass(SUCCESS_ROW_CLASS);
                autoScrolling();
                PC++;
                this.function.apply(this, this.args);
                loadHtmlArray(stack,STACK_CONTAINER_SELECTOR);
                loadHtmlArray(data,DATA_CONTAINER_SELECTOR);
                updatePointers();
            }
            else{
                throw new Error("Number of arguments doesn't match.")
            }
        }catch (e) {
            console.log(e.message);
        }
    }
}
class StackLine {
    constructor(value){
        if(!validateArrayOverflow(stack,SP,STACK_SIZE)){
            this.value = value;
            this.number = SP;
            SP++;
        }
    }
}
class DataLine{
    constructor(address, value){
        if(!validateArrayOverflow(data,address,DATA_SIZE)){
            this.address = address;
            this.value = value;
        }
    }
}
// Setup
$(document).ready(function(){
    loadTemplates();
    toolbar = new Toolbar();
    loadHtmlArray(data, DATA_CONTAINER_SELECTOR);
});
// Helper functions
function loadTemplates(){
    $template_from_html = $('#template').html();
    clean_template = cleanLine($template_from_html);
    $templates = $(clean_template);
}
function readCode(program){
    let program_lines = splitInstructions(program);
    let i=1;
    instructions = [];
    program_lines.forEach(line => {
        let instruction = new Instruction(line, i);
        instructions.push(instruction);
        createOrUpdateDomNode(instruction, true);
        i++;
    });
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
    PC = SP = MP = 0;
    $('.instruction').removeClass(SUCCESS_ROW_CLASS);
    $('#toolbar').remove();
    $('#console-body').empty();
    $('#console-body').append('>&nbsp;');
    autoScrolling();
    toolbar = new Toolbar();
    stack = [];
    data = Array(DATA_SIZE).fill({});
    loadHtmlArray(stack,STACK_CONTAINER_SELECTOR);
    loadHtmlArray(data, DATA_CONTAINER_SELECTOR);
    updatePointers();
}
function autoScrolling(){
    let consol = document.getElementById('console');
    consol.scrollTop = consol.scrollHeight;
    let instruction = document.getElementById('body-instructions');
    instruction.scrollTop = (instruction.scrollHeight/instructions.length)*(PC);
    let stack_table = document.getElementById('body-stack');
    stack_table.scrollTop = stack_table.scrollHeight;
}
function validateArrayOverflow(array, index, max_size){
    let overflow = false;
    if(index > max_size-1){
        overflow = true;
        executionErrorMessage();
        haltProgram();
    }
    return overflow;
}
function executionErrorMessage(){
    $("#alert-container").append("<div class='alert alert-danger alert-dismissible' role='alert'>"+
        "<button type='button' class='close' data-dismiss='alert' aria-label='Close'>"+
        "<span aria-hidden='true'>&times;</span></button>"+
        "<strong>Error de ejecución</strong>"+
        "</div>");
}
function updatePointers(){
    $("#programPointer").val(PC);
    console.log(PC);
    $("#stackPointer").val(SP);
    $("#markPointer").val(MP);
}
//When file is selected
$(document).on('change', ':file', function () {
    var input = $(this),
        numFiles = input.get(0).files ? input.get(0).files.length : 1,
        label = input.val().replace(/\\/g, '/').replace(/.*\//, '');
    input.trigger('fileselect', [numFiles, label]);

    var reader = new FileReader();
    reader.onload = function (e) {
        haltProgram();
        $('#body-instructions').html("");
        readCode(reader.result);
    };
    reader.readAsText(input.get(0).files[0]);
});
//Shows file name when it is selected with the input
$(':file').on('fileselect', function (event, numFiles, label) {
    var input = $(this).parents('.input-group').find(':text');
    input.val(label);
});