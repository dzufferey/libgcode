# libgcode

A scala library to parse, transform, and print g-code files

## Status

I just started on that.
For the moment, it just parses and prints basic g-code files.

## ToDo

#### Extractors

Some more extractors for commands.
For instance:
* `case G(1, params) => ...`.
* `case G(1.5, X(var1) :: remainingParams ) => ...`.
* ...

#### Examples of Transformers/Transducers

A few sample classes of g-code transformers:
* remove comments
* change unit (inch to mm and return)
* scale, translate, rotate
* circular motion to segments (given a maximal acceptable error)

#### Abstract Machine

have an abstract machine to simulate the code, i.e., get the state of the machine at any point in time.

The state of the machine is:
* position: x,y,z
* orientation: i,j,k
* unit: inch | mm
* feedrate: unit / min
* plane: XY | ZX | YZ
* position: absolute | relative
* spindle: off | clockwise | counter-clockwise (RPM?)
* coolant: off | mist | flood
* ? cutter radius compensation
* ? tool lenght offset
* ? extruder(s) feedrate/position
* ? value of other parameters
