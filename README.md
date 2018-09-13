<p align="center">
  <img src="unetLogo.png">
</p>

# Compilador TINY a P-Code y Máquina Virtual para ejecutar P-Code.

## Compilador
El compilador está programado en JAVA y hace uso del generador léxico **JFlex** junto con el generador sintáctico **CUP**. Está diseñado de una manera sencilla para el entendimiento básico sobre los compiladores. Este compilador está basado en un compilador Tiny que genera codigo Tiny Machine (TM). 

Inicialmente, el compilador base solo permitía instrucciones de suma, resta, multiplicación, división, comparación (igual a y menor que), condiciones, un ciclo básico, y lectura y escritura de valores. Esta versión incluye soporte para vectores y funciones (sin recursividad). 

Dentro de las especificaciones léxicas se encuentran: 


| Palabras Reservadas    | Símbolos Especiales     | Otros                       |
| -----------------------|:-----------------------:|-----------------------------|
| if                     | +                       | Números enteros             |  
| then                   | -                       | Identificadores (variables) |
| else                   | *                       | Vectores con índices enteros|
| end                    | /                       | Funciones con retorno       |
| repeat                 | =                       |                             |
| until                  | <                       |                             |
| read                   | (                       |                             |
| write                  | )                       |                             |
| function               | ;                       |                             |
| return                 | :=                      |                             |
| vector                 | ,                       |                             |
|                        | [                       |                             |
|                        | ]                       |                             |

Cada variable debe ser declarada antes de ser usada en una de las siguientes operaciones:
* Al lado derecho de las asignaciones
* Como parámetro de una función 
* Como índice de un vector

Cada vector debe ser declarado antes de ser usado, y presentan el formato **vector A n**, siendo **vector** la palabra reservada, **A** el nombre del vector y **n** el tamaño del vector en entero.

Las funciones siempre retornarán un valor. El paso de parametros en las funciones es por valor.

> Para el código fuente es importante resaltar que la última sentencia de cada bloque NO DEBE LLEVAR PUNTO Y COMA ";"

Por ejemplo: 
```python
x:=3; { Bloque 1 }
vector A 10; { Bloque 1 }
read x; { Bloque 1 }
A[2+x]:=1; { Bloque 1 }
if(x > 0) { Bloque 1 }
then 
  write x { Bloque 2 }
end;
write A[2+x] { Bloque 1 }
```

Cada regla gramatical tiene su respectiva clase definida para actuar como un nodo. Los objetos de dichas clases (nodos) almacenan y relacionan la información correspondiente desde el parser (analizador sintáctico). El parser aplica las reglas de manera ascendente (análisis LR) y arma el árbol sintáctico (usando los nodos declarados). Una vez acabado el análisis sintáctico, se realiza un recorrido preorden para insertar los identificadores de variables, funciones y vectores en la tabla de símbolos. Luego de realizar estas tareas, el compilador procede a generar el código P en un recorrido preorden, usando funciones para cada tipo de sentencia, las cuales a su vez usan recursivamente otras funciones generadoras de código que van completando las instrucciones correspondientes en código P. La generación de código para la declaración de funciones es realizada al final. Después de esto, el compilador realiza una última pasada para reemplazar las etiquetas (labels) con los respectivos números de instrucción a los cuales identifica.

Las reglas semánticas implementadas son las siguientes:
* Declaración de identificadores, funciones y vectores, antes de ser usados.
* Comparaciones incorrectas que se puedan dar en el índice de un vector. Ejemplo: ```A[(x<0)]``` ó ```A[(x=0)]```

El código P generado es escrito en un archivo con extensión **.pcod** que es indicado antes del inicio de la compilación. (si no se indica, el compilador generara el archivo en la misma ubcación que el archivo fuente, con extensión .pcod)

## Máquina Virtual P-Code

![VM](vm.gif)
