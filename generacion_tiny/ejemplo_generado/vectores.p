LDA    0        ;cargar direccion de identificador: a
LDC    1        ;cargar constante: 1
IXA    elem_size(0)        ;cargar direccion de identificador: a
LDC    3        ;cargar constante: 3
STO            ;asignacion: almaceno el valor para el id a
LDA    5        ;cargar direccion de identificador: b
LDA    0        ;cargar direccion de la variable: a
LDC    1        ;cargar constante: 1
IXA    elem_size(0)        ;cargar la direccion de la posicion del vector: a
IND    0        ;cargar el valor de la direccion anterior
LDA    0        ;cargar direccion de la variable: a
LDC    2        ;cargar constante: 2
IXA    elem_size(0)        ;cargar la direccion de la posicion del vector: a
IND    0        ;cargar el valor de la direccion anterior
ADI            ;Operación suma: +
STO            ;asignacion: almaceno el valor para el id b
