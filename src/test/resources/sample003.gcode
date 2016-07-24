M104 S200 ; set temperature
G28 ; home all axes
M109 S200 ; wait for temperature to be reached
G21 ; set units to millimeters
G90 ; use absolute coordinates
M82 ; use absolute distances for extrusion
G1 Z5 F5000 ; lift nozzle
G1 E-2.00000 F2400.00000
G92 E0
G1 X61.337 Y61.337 F6000.000
G1 E2.00000 F2400.00000
G1 X61.892 Y60.811 E2.01635 F1080.000
G1 X62.271 Y60.490 E2.02695
M104 S0 ; turn off temperature
M140 S0 ; turn off bed
G28 X0  ; home X axis
M84     ; disable motors
