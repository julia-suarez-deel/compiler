function cleanLine(line){
    // Remove tabs, more than two spaces, EOL and carriage return
    let clean_line = line.replace(/(\r|\s{2,}|;.*)+/g,'');
    // For minify html too
    clean_line.replace(/>\s+</,'><');
    return clean_line;
}