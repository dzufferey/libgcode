; KISSlicer - FREE
; Linux
; version 1.5 Release Linux64
; Built: Aug  4 2016, 11:43:44
; Running on 8 cores
;
; Saved: Fri Sep 23 18:15:36 2016
; 't_slot_nut_m4.gcode'
;
; *** Printer Settings ***
;
; printer_name = sample printer
; bed_STL_filename = 
; extension = gcode
; cost_per_hour = 0
; g_code_prefix = 3B205B6D6D5D206D6F64650A4732310A3B206162736F
;     6C757465206D6F64650A4739300A
; > Decoded
; >   ; [mm] mode
; >   G21
; >   ; absolute mode
; >   G90
; g_code_warm = 3B2053656C6563742065787472756465722C207761726D
;     2C2070757267650A0A3B204266422D7374796C650A4D3C4558542B31
;     3E303420533C54454D503E0A4D3534320A4D35353C4558542B313E20
;     50333230303020533930300A4D3534330A0A3B2035442D7374796C65
;     0A543C4558542B303E0A4D31303920533C54454D503E0A
; > Decoded
; >   ; Select extruder, warm, purge
; >   ; BfB-style
; >   M<EXT+1>04 S<TEMP>
; >   M542
; >   M55<EXT+1> P32000 S900
; >   M543
; >   ; 5D-style
; >   T<EXT+0>
; >   M109 S<TEMP>
; g_code_same_warm = NULL
; > Decoded
; g_code_same_cool = NULL
; > Decoded
; g_code_cool = 3B2047756172616E746565642073616D65206578747275
;     6465722C206275742061626F757420746F20646573656C6563742C20
;     6D617962652072657472616374206265666F726520636F6F6C696E67
;     20646F776E0A0A3B204266422D7374796C650A4D3C4558542B313E30
;     3420533C54454D503E0A0A3B2035442D7374796C650A4D3130342053
;     3C54454D503E0A
; > Decoded
; >   ; Guaranteed same extruder, but about to deselect, maybe
; >    retract before cooling down
; >   ; BfB-style
; >   M<EXT+1>04 S<TEMP>
; >   ; 5D-style
; >   M104 S<TEMP>
; g_code_N_layers = 3B204D617962652072652D686F6D65205820262059
;     3F
; > Decoded
; >   ; Maybe re-home X & Y?
; g_code_postfix = 3B20416C6C207573656420657874727564657273206
;     1726520616C72656164792027436F6F6C65642720746F20300A
; > Decoded
; >   ; All used extruders are already 'Cooled' to 0
; post_process = NULL
; > Decoded
; every_N_layers = 0
; num_extruders = 1
; firmware_type = 1
; add_comments = 1
; fan_on = M106
; fan_off = M107
; fan_pwm = 1
; add_m101_g10 = 0
; z_speed_mm_per_s = 3.5
; z_settle_mm = 0.25
; bed_size_x_mm = 100
; bed_size_y_mm = 100
; bed_size_z_mm = 100
; ext_radius = 0
; bed_offset_x_mm = 0
; bed_offset_y_mm = 0
; bed_offset_z_mm = 0
; bed_roughness_mm = 0.25
; round_bed = 0
; travel_speed_mm_per_s = 500
; rim_speed_mm_per_s = 250
; first_layer_speed_mm_per_s = 10
; dmax_per_layer_mm_per_s = 50
; xy_accel_mm_per_s_per_s = 1500
; xy_steps_per_mm = 150
; lo_speed_perim_mm_per_s = 5
; lo_speed_loops_mm_per_s = 20
; lo_speed_solid_mm_per_s = 15
; lo_speed_sparse_mm_per_s = 30
; hi_speed_perim_mm_per_s = 15
; hi_speed_loops_mm_per_s = 65
; hi_speed_solid_mm_per_s = 60
; hi_speed_sparse_mm_per_s = 75
; ext_gain_1 = 1
; ext_material_1 = 0
; ext_axis_1 = 0
; ext_gain_2 = 1
; ext_material_2 = 0
; ext_axis_2 = 0
; ext_Xoff_2 = 0
; ext_Yoff_2 = 0
; ext_gain_3 = 1
; ext_material_3 = 0
; ext_axis_3 = 0
; ext_Xoff_3 = 0
; ext_Yoff_3 = 0
; ext_gain_4 = 1
; ext_material_4 = 0
; ext_axis_4 = 0
; ext_Xoff_4 = 0
; ext_Yoff_4 = 0
; model_ext = 0
; support_ext = 0
; support_body_ext = 0
; raft_ext = 0
; ext_order_optimize = 0
; solid_loop_overlap_fraction = 0.5
;
; *** Material Settings for Extruder 1 ***
;
; material_name = sample material
; g_code_matl = 3B204D617962652073657420736F6D65206D6174657269
;     616C2D737065636966696320472D636F64653F
; > Decoded
; >   ; Maybe set some material-specific G-code?
; fan_Z_mm = 0
; fan_loops_percent = 100
; fan_inside_percent = 0
; fan_cool_percent = 100
; temperature_C = 250
; keep_warm_C = 180
; first_layer_C = 255
; bed_C = 80
; sec_per_C_per_C = 0
; flow_min_mm3_per_s = 0.01
; flow_max_mm3_per_s = 10
; destring_suck = 1.25
; destring_prime = 1.25
; destring_min_mm = 1
; destring_trigger_mm = 100
; destring_speed_mm_per_s = 15
; destring_suck_speed_mm_per_s = 15
; Z_lift_mm = 0
; min_layer_time_s = 10
; wipe_mm = 10
; cost_per_cm3 = 0
; flowrate_tweak = 1
; fiber_dia_mm = 3
; color = 0
;
; *** Style Settings ***
;
; style_name = sample style
; quality_percentage = 50
; layer_thickness_mm = 0.25
; extrusion_width_mm = 0.5
; num_loops = 3
; skin_thickness_mm = 0.8
; infill_extrusion_width = 0.5
; infill_density_denominator = 4
; stacked_layers = 1
; use_destring = 1
; use_wipe = 1
; use_corners = 1
; loops_insideout = 0
; infill_st_oct_rnd = 1
; inset_surface_xy_mm = 0
; seam_jitter_degrees = 0
; seam_depth_scaler = 1
; seam_gap_scaler = 1
; seam_angle_degrees = 45
;
; *** Support Settings ***
;
; support_name = sample support
; support_sheathe = 0
; support_density = 3
; solid_interface = 0
; use_lower_interface = 1
; support_inflate_mm = 0
; support_gap_mm = 0.5
; support_angle_deg = 45
; support_z_max_mm = -1
; sheathe_z_max_mm = -1
; raft_mode = 0
; prime_pillar_mode = 0
; raft_inflate_mm = 2
; raft_hi_stride_mm = 1.5
; raft_hi_thick_mm = 0.25
; raft_hi_width_mm = 0.35
; raft_lo_stride_mm = 2.5
; raft_lo_thick_mm = 0.25
; raft_lo_width_mm = 0.5
; brim_mm = 0
; brim_ht_mm = 0
; brim_fillet = 1
; interface_band_mm = 2
; interface_extrusion_gain = 1
; interface_Z_gap_mm = 0
; raft_interface_layers = 0
;
; *** Actual Slicing Settings As Used ***
;
; layer_thickness_mm = 0.25
; extrusion_width = 0.5
; num_ISOs = 3
; wall_thickness = 0.8
; infill_style = 5
; support_style = 3
; solid_interface = 0
; use_lower_interface = 1
; support_angle = 44.9
; destring_min_mm = 1
; stacked_infill_layers = 1
; raft_style = 0
; raft_sees_prime_paths = 0
; raft_extra_interface_layers = 0
; raft_hi_stride_mm = 0
; raft_lo_stride_mm = 0
; oversample_res_mm = 0.125
; crowning_threshold_mm = 1
; loops_insideout = 0
; solid_loop_overlap_fraction = 0.5
; inflate_raft_mm = 0
; inflate_support_mm = 0
; model_support_gap_mm = 0.5
; brim_mm = 0
; brim_ht_mm = 0
; infill_st_oct_rnd = 1
; support_Z_max_mm = 1e+20
; sheathe_Z_max_mm = 0
; inset_surface_xy_mm = 0
; seam_jitter_degrees = 0
; seam_depth_scaler = 1
; seam_gap_scaler = 1
; seam_angle_degrees = 45
; seam_use_corners = 1
; interface_band_mm = 2
; skip_N_support_layers = 0
; Speed vs Quality = 0.50
; Perimeter Speed = 10.00
; Loops Speed = 42.50
; Solid Speed = 37.50
; Sparse Speed = 52.50
;
; *** G-code Prefix ***
;
; [mm] mode
G21
; absolute mode
G90
;
; *** Main G-code ***
;
; BEGIN_LAYER_OBJECT z=0.500
;
; *** Selecting and Warming Extruder 1 to 255 C ***
; Select extruder, warm, purge
; BfB-style
M104 S255
M542
M551 P32000 S900
M543
; 5D-style
T0
M109 S255
;
;
; fan %*255
M106 S255
;
; 'Perimeter Path', 0.4 [feed mm/s], 10.0 [head mm/s]
G1 X4.96 Y6.81 Z0.75 E0 F30000
G1 X4.96 Y6.81 Z0.5 E0 F210
; 'Destring Prime'
G1 E1.25 F900
G1 X5.05 Y7.24 E0.0155 F600
G1 X-5.13 Y7.23 E0.36
G1 X-5.23 Y7.18 E0.0042
G1 X-5.23 Y-7.18 E0.5077
G1 X-5.14 Y-7.23 E0.0037
G1 X5.14 Y-7.23 E0.3637
G1 X5.23 Y-7.18 E0.0038
G1 X5.23 Y6.98 E0.5007
G1 X4.84 Y6.93 E0.014
;
; 'Loop Path', 0.4 [feed mm/s], 10.0 [head mm/s]
G1 X4.54 Y6.73 E0 F30000
G1 X-4.61 Y6.73 E0.3236 F600
G1 X-4.73 Y6.67 E0.0046
G1 X-4.73 Y-6.67 E0.4719
G1 X-4.61 Y-6.73 E0.0047
G1 X4.61 Y-6.73 E0.3258
G1 X4.73 Y-6.67 E0.0047
G1 X4.73 Y6.47 E0.465
;
; 'Loop Path', 0.4 [feed mm/s], 10.0 [head mm/s]
G1 X4.04 Y6.23 E0 F30000
G1 X-4.1 Y6.23 E0.288 F600
G1 X-4.22 Y6.17 E0.0047
G1 X-4.22 Y-6.17 E0.4364
G1 X-4.1 Y-6.23 E0.0047
G1 X4.1 Y-6.23 E0.2903
G1 X4.22 Y-6.17 E0.0048
G1 X4.22 Y5.97 E0.4294
;
; 'Perimeter Path', 0.4 [feed mm/s], 10.0 [head mm/s]
G1 X1.85 Y1.66 E0 F30000
G1 X1.45 Y1.73 E0.0142 F600
G1 X1.13 Y1.96 E0.014
G1 X0.72 Y2.16 E0.0159
G1 X0.15 Y2.27 E0.0206
G1 X-0.45 Y2.23 E0.0216
G1 X-0.95 Y2.07 E0.0184
G1 X-1.5 Y1.71 E0.023
G1 X-1.85 Y1.32 E0.0187
G1 X-2.14 Y0.76 E0.0223
G1 X-2.26 Y0.21 E0.0198
G1 X-2.24 Y-0.38 E0.0211
G1 X-2.1 Y-0.86 E0.0174
G1 X-1.8 Y-1.38 E0.0216
G1 X-1.44 Y-1.76 E0.0183
G1 X-0.93 Y-2.07 E0.021
G1 X-0.43 Y-2.23 E0.0188
G1 X0.16 Y-2.27 E0.0209
G1 X0.75 Y-2.15 E0.0212
G1 X1.3 Y-1.86 E0.0222
G1 X1.69 Y-1.52 E0.0182
G1 X2.03 Y-1.02 E0.0213
G1 X2.2 Y-0.58 E0.0166
G1 X2.27 Y0.02 E0.0215
G1 X2.2 Y0.59 E0.0203
G1 X2.03 Y1.02 E0.0163
G1 X1.73 Y1.46 E0.0186
G1 X1.68 Y1.84 E0.0139
;
; 'Loop Path', 0.4 [feed mm/s], 10.0 [head mm/s]
G1 X1.81 Y2.08 E0 F30000
G1 X1.39 Y2.4 E0.0184 F600
G1 X0.85 Y2.64 E0.0209
G1 X0.14 Y2.77 E0.0256
G1 X-0.57 Y2.72 E0.0254
G1 X-1.25 Y2.48 E0.0255
G1 X-1.85 Y2.07 E0.0254
G1 X-2.28 Y1.59 E0.0229
G1 X-2.59 Y0.99 E0.0238
G1 X-2.76 Y0.31 E0.0248
G1 X-2.76 Y-0.33 E0.0228
G1 X-2.56 Y-1.09 E0.0278
G1 X-2.18 Y-1.71 E0.0256
G1 X-1.69 Y-2.2 E0.0244
G1 X-1.13 Y-2.53 E0.0232
G1 X-0.48 Y-2.74 E0.0242
G1 X0.22 Y-2.77 E0.0246
G1 X0.92 Y-2.62 E0.0254
G1 X1.55 Y-2.3 E0.0251
G1 X2.06 Y-1.86 E0.0238
G1 X2.46 Y-1.28 E0.025
G1 X2.7 Y-0.67 E0.0231
G1 X2.78 Y0.04 E0.0251
G1 X2.68 Y0.73 E0.0245
G1 X2.43 Y1.34 E0.0237
G1 X2.09 Y1.81 E0.0204
;
; 'Loop Path', 0.4 [feed mm/s], 10.0 [head mm/s]
G1 X2.16 Y2.44 E0 F30000
G1 X1.65 Y2.83 E0.0227 F600
G1 X1.01 Y3.12 E0.0249
G1 X0.23 Y3.27 E0.0281
G1 X-0.62 Y3.22 E0.0302
G1 X-1.5 Y2.92 E0.0327
G1 X-2.14 Y2.48 E0.0277
G1 X-2.69 Y1.87 E0.029
G1 X-3.04 Y1.23 E0.0257
G1 X-3.26 Y0.4 E0.0302
G1 X-3.25 Y-0.42 E0.0291
G1 X-3.03 Y-1.27 E0.0313
G1 X-2.59 Y-2.01 E0.0302
G1 X-2.06 Y-2.55 E0.0269
G1 X-1.38 Y-2.97 E0.0282
G1 X-0.58 Y-3.23 E0.0297
G1 X0.25 Y-3.27 E0.0295
G1 X1.1 Y-3.09 E0.0306
G1 X1.84 Y-2.72 E0.0294
G1 X2.46 Y-2.16 E0.0295
G1 X2.89 Y-1.55 E0.0265
G1 X3.18 Y-0.82 E0.0276
G1 X3.28 Y0.07 E0.0317
G1 X3.17 Y0.86 E0.0282
G1 X2.88 Y1.56 E0.0267
G1 X2.44 Y2.17 E0.0265
; fan off
M107
;
; 'Solid Path', 0.4 [feed mm/s], 10.0 [head mm/s]
G1 X3.4 Y-1.28 E0 F30000
G1 X3.77 Y-1.65 E0.0185 F600
G1 X3.85 Y-2.44 E0.0279
G1 X3.24 Y-1.82 E0.0306
G1 X2.95 Y-2.25 E0.018
G1 X3.77 Y-3.07 E0.0411
G1 X3.77 Y-3.77 E0.025
G1 X2.63 Y-2.63 E0.0572
G1 X2.25 Y-2.95 E0.0178
G1 X3.77 Y-4.48 E0.0763
G1 X3.77 Y-5.19 E0.025
G1 X1.83 Y-3.24 E0.0975
G1 X1.34 Y-3.46 E0.0189
G1 X3.65 Y-5.78 E0.1156
G1 X2.95 Y-5.78 E0.025
G1 X0.8 Y-3.63 E0.1075
G1 X0.17 Y-3.7 E0.0225
G1 X2.24 Y-5.78 E0.1037
G1 X1.53 Y-5.78 E0.025
G1 X-0.53 Y-3.71 E0.1035
G1 X-1.64 Y-3.31 E0.0416
G1 X0.83 Y-5.78 E0.1234
G1 X0.12 Y-5.78 E0.0251
G1 X-3.77 Y-1.88 E0.1947
G1 X-3.77 Y-2.59 E0.025
G1 X-0.59 Y-5.78 E0.1594
G1 X-1.29 Y-5.78 E0.025
G1 X-3.77 Y-3.3 E0.124
G1 X-3.77 Y-4 E0.025
G1 X-2 Y-5.78 E0.0887
G1 X-2.71 Y-5.78 E0.025
G1 X-3.77 Y-4.71 E0.0532
G1 X-3.75 Y-5.44 E0.0257
G1 X-3.39 Y-5.8 E0.0179
;
; 'Solid Path', 0.4 [feed mm/s], 10.0 [head mm/s]
G1 X-3.46 Y1.34 E0 F30000
G1 X-3.74 Y1.62 E0.0141 F600
G1 X-3.77 Y2.36 E0.0261
G1 X-3.23 Y1.82 E0.0269
G1 X-2.96 Y2.25 E0.0182
G1 X-3.77 Y3.07 E0.0408
G1 X-3.77 Y3.77 E0.025
G1 X-2.62 Y2.62 E0.0575
G1 X-2.25 Y2.96 E0.0177
G1 X-3.77 Y4.48 E0.0762
G1 X-3.77 Y5.19 E0.025
G1 X-1.82 Y3.24 E0.0975
G1 X-1.34 Y3.46 E0.0189
G1 X-3.65 Y5.78 E0.1158
G1 X-2.95 Y5.78 E0.0251
G1 X-0.81 Y3.64 E0.1069
G1 X-0.17 Y3.7 E0.0229
G1 X-2.24 Y5.78 E0.1037
G1 X-1.53 Y5.78 E0.0251
G1 X0.55 Y3.69 E0.1041
G1 X1.64 Y3.31 E0.041
G1 X-0.83 Y5.78 E0.1234
G1 X-0.12 Y5.78 E0.025
G1 X3.77 Y1.88 E0.1948
G1 X3.77 Y2.59 E0.025
G1 X0.59 Y5.78 E0.1594
G1 X1.29 Y5.78 E0.025
G1 X3.77 Y3.3 E0.124
G1 X3.77 Y4 E0.025
G1 X2 Y5.78 E0.0887
G1 X2.71 Y5.78 E0.025
G1 X3.77 Y4.71 E0.0532
G1 X3.75 Y5.44 E0.0257
G1 X3.34 Y5.85 E0.0205
;
; 'Destring/Wipe/Jump Path', 0.0 [feed mm/s], 10.0 [head mm/s]
; 'Destring Suck'
G1 E-1.25 F900
G1 X3.75 Y5.44 E0 F600
G1 X3.77 Y4.71 E0
G1 X2.71 Y5.78 E0
G1 X2 Y5.78 E0
G1 X3.77 Y4 E0
G1 X3.77 Y3.3 E0
G1 X1.46 Y5.61 E0
;
; Post-layer lift
G1 X1.46 Y5.61 Z1 E0 F210
; END_LAYER_OBJECT z=0.50
; BEGIN_LAYER_OBJECT z=0.750
;
; *** Cooling Same Extruder (1) to 250 C ***
; Guaranteed same extruder, but about to deselect, maybe retract before cooling down
; BfB-style
M104 S250
; 5D-style
M104 S250
;
;
; fan %*255
M106 S255
;
; 'Perimeter Path', 0.2 [feed mm/s], 10.0 [head mm/s]
G1 X4.96 Y6.81 Z1 E0 F30000
G1 X4.96 Y6.81 Z0.75 E0 F210
; 'Destring Prime'
G1 E1.25 F900
G1 X5.05 Y7.24 E0.0077 F600
G1 X-5.13 Y7.23 E0.18
G1 X-5.23 Y7.18 E0.0022
G1 X-5.23 Y-7.18 E0.2538
G1 X-5.14 Y-7.23 E0.0019
G1 X5.14 Y-7.23 E0.1818
G1 X5.23 Y-7.18 E0.0019
G1 X5.23 Y6.98 E0.2503
G1 X4.84 Y6.93 E0.007
;
; 'Loop Path', 0.5 [feed mm/s], 26.2 [head mm/s]
G1 X4.54 Y6.73 E0 F30000
G1 X-4.61 Y6.73 E0.1618 F1575
G1 X-4.73 Y6.67 E0.0024
G1 X-4.73 Y-6.67 E0.2359
G1 X-4.61 Y-6.73 E0.0024
G1 X4.61 Y-6.73 E0.1629
G1 X4.73 Y-6.67 E0.0023
G1 X4.73 Y6.47 E0.2325
;
; 'Loop Path', 0.8 [feed mm/s], 42.5 [head mm/s]
G1 X4.04 Y6.23 E0 F30000
G1 X-4.1 Y6.23 E0.144 F2550
G1 X-4.22 Y6.17 E0.0024
G1 X-4.22 Y-6.17 E0.2182
G1 X-4.1 Y-6.23 E0.0023
G1 X4.1 Y-6.23 E0.1452
G1 X4.22 Y-6.17 E0.0023
G1 X4.22 Y5.97 E0.2147
;
; 'Perimeter Path', 0.2 [feed mm/s], 10.0 [head mm/s]
G1 X1.85 Y1.67 E0 F30000
G1 X1.45 Y1.73 E0.0072 F600
G1 X1.24 Y1.9 E0.0048
G1 X0.74 Y2.15 E0.0099
G1 X0.17 Y2.27 E0.0102
G1 X-0.42 Y2.24 E0.0106
G1 X-0.95 Y2.07 E0.0098
G1 X-1.5 Y1.71 E0.0115
G1 X-1.83 Y1.34 E0.0089
G1 X-2.12 Y0.83 E0.0103
G1 X-2.26 Y0.23 E0.0109
G1 X-2.25 Y-0.3 E0.0094
G1 X-2.11 Y-0.85 E0.0101
G1 X-1.8 Y-1.38 E0.0109
G1 X-1.44 Y-1.76 E0.0091
G1 X-0.94 Y-2.07 E0.0104
G1 X-0.42 Y-2.24 E0.0097
G1 X0.17 Y-2.27 E0.0105
G1 X0.75 Y-2.15 E0.0104
G1 X1.32 Y-1.86 E0.0114
G1 X1.74 Y-1.45 E0.0104
G1 X2.03 Y-1.01 E0.0093
G1 X2.2 Y-0.59 E0.0079
G1 X2.28 Y0.01 E0.0109
G1 X2.19 Y0.6 E0.0104
G1 X2.03 Y1.02 E0.0079
G1 X1.73 Y1.46 E0.0094
G1 X1.68 Y1.85 E0.007
;
; 'Loop Path', 0.5 [feed mm/s], 26.2 [head mm/s]
G1 X1.81 Y2.08 E0 F30000
G1 X1.5 Y2.34 E0.007 F1575
G1 X0.9 Y2.63 E0.0119
G1 X0.15 Y2.78 E0.0134
G1 X-0.56 Y2.73 E0.0127
G1 X-1.25 Y2.48 E0.013
G1 X-1.85 Y2.07 E0.0127
G1 X-2.28 Y1.59 E0.0114
G1 X-2.59 Y1 E0.0119
G1 X-2.76 Y0.3 E0.0127
G1 X-2.76 Y-0.32 E0.0109
G1 X-2.56 Y-1.09 E0.0141
G1 X-2.18 Y-1.71 E0.0128
G1 X-1.71 Y-2.19 E0.0119
G1 X-1.19 Y-2.5 E0.0107
G1 X-0.48 Y-2.74 E0.0133
G1 X0.21 Y-2.77 E0.0123
G1 X0.92 Y-2.63 E0.0127
G1 X1.57 Y-2.3 E0.0129
G1 X2.08 Y-1.84 E0.0121
G1 X2.46 Y-1.28 E0.0121
G1 X2.7 Y-0.66 E0.0116
G1 X2.79 Y0.07 E0.0131
G1 X2.67 Y0.74 E0.0119
G1 X2.43 Y1.35 E0.0116
G1 X2.09 Y1.81 E0.0102
;
; 'Loop Path', 0.8 [feed mm/s], 42.5 [head mm/s]
G1 X2.16 Y2.44 E0 F30000
G1 X1.76 Y2.78 E0.0093 F2550
G1 X1.03 Y3.12 E0.0142
G1 X0.22 Y3.28 E0.0146
G1 X-0.66 Y3.22 E0.0157
G1 X-1.5 Y2.91 E0.0158
G1 X-2.14 Y2.48 E0.0137
G1 X-2.7 Y1.86 E0.0147
G1 X-3.05 Y1.19 E0.0133
G1 X-3.26 Y0.39 E0.0147
G1 X-3.26 Y-0.41 E0.0142
G1 X-3.03 Y-1.27 E0.0157
G1 X-2.59 Y-2.01 E0.0151
G1 X-2.06 Y-2.55 E0.0135
G1 X-1.39 Y-2.97 E0.014
G1 X-0.57 Y-3.24 E0.0151
G1 X0.26 Y-3.27 E0.0147
G1 X1.1 Y-3.09 E0.0152
G1 X1.85 Y-2.72 E0.015
G1 X2.48 Y-2.14 E0.0149
G1 X2.89 Y-1.54 E0.0129
G1 X3.18 Y-0.83 E0.0136
G1 X3.29 Y0.07 E0.016
G1 X3.16 Y0.87 E0.0144
G1 X2.88 Y1.56 E0.0131
G1 X2.44 Y2.17 E0.0133
; fan off
M107
;
; 'Solid Path', 0.7 [feed mm/s], 37.5 [head mm/s]
G1 X3.4 Y1.28 E0 F30000
G1 X3.77 Y1.65 E0.0092 F2250
G1 X3.85 Y2.44 E0.014
G1 X3.24 Y1.82 E0.0152
G1 X2.96 Y2.25 E0.0091
G1 X3.77 Y3.07 E0.0204
G1 X3.77 Y3.77 E0.0125
G1 X2.63 Y2.63 E0.0286
G1 X2.26 Y2.96 E0.0089
G1 X3.77 Y4.48 E0.038
G1 X3.77 Y5.19 E0.0125
G1 X1.83 Y3.24 E0.0487
G1 X1.35 Y3.47 E0.0094
G1 X3.65 Y5.78 E0.0578
G1 X2.95 Y5.78 E0.0125
G1 X0.8 Y3.63 E0.0537
G1 X0.17 Y3.71 E0.0112
G1 X2.24 Y5.78 E0.0518
G1 X1.53 Y5.78 E0.0125
G1 X-0.53 Y3.71 E0.0516
G1 X-1.64 Y3.31 E0.0209
G1 X0.83 Y5.78 E0.0617
G1 X0.12 Y5.78 E0.0125
G1 X-3.77 Y1.88 E0.0974
G1 X-3.77 Y2.59 E0.0125
G1 X-0.59 Y5.78 E0.0797
G1 X-1.29 Y5.78 E0.0125
G1 X-3.77 Y3.3 E0.062
G1 X-3.77 Y4 E0.0125
G1 X-2 Y5.78 E0.0443
G1 X-2.71 Y5.78 E0.0125
G1 X-3.77 Y4.71 E0.0266
G1 X-3.75 Y5.44 E0.0129
G1 X-3.39 Y5.8 E0.0089
;
; 'Solid Path', 0.7 [feed mm/s], 37.5 [head mm/s]
G1 X-3.46 Y-1.34 E0 F30000
G1 X-3.74 Y-1.62 E0.0071 F2250
G1 X-3.77 Y-2.36 E0.013
G1 X-3.23 Y-1.82 E0.0135
G1 X-2.96 Y-2.25 E0.0091
G1 X-3.77 Y-3.07 E0.0204
G1 X-3.77 Y-3.77 E0.0125
G1 X-2.62 Y-2.62 E0.0287
G1 X-2.25 Y-2.96 E0.0089
G1 X-3.77 Y-4.48 E0.0381
G1 X-3.77 Y-5.19 E0.0125
G1 X-1.82 Y-3.24 E0.0488
G1 X-1.34 Y-3.46 E0.0094
G1 X-3.65 Y-5.78 E0.0579
G1 X-2.95 Y-5.78 E0.0125
G1 X-0.81 Y-3.64 E0.0534
G1 X-0.17 Y-3.71 E0.0114
G1 X-2.24 Y-5.78 E0.0518
G1 X-1.53 Y-5.78 E0.0125
G1 X0.55 Y-3.69 E0.0521
G1 X1.64 Y-3.31 E0.0205
G1 X-0.83 Y-5.78 E0.0617
G1 X-0.12 Y-5.78 E0.0125
G1 X3.77 Y-1.88 E0.0974
G1 X3.77 Y-2.59 E0.0125
G1 X0.59 Y-5.78 E0.0797
G1 X1.29 Y-5.78 E0.0125
G1 X3.77 Y-3.3 E0.062
G1 X3.77 Y-4 E0.0125
G1 X2 Y-5.78 E0.0443
G1 X2.71 Y-5.78 E0.0125
G1 X3.77 Y-4.71 E0.0266
G1 X3.75 Y-5.44 E0.0129
G1 X3.34 Y-5.85 E0.0103
;
; 'Destring/Wipe/Jump Path', 0.0 [feed mm/s], 52.5 [head mm/s]
; 'Destring Suck'
G1 E-1.25 F900
G1 X3.75 Y-5.44 E0 F3150
G1 X3.77 Y-4.71 E0
G1 X2.71 Y-5.78 E0
G1 X2 Y-5.78 E0
G1 X3.77 Y-4 E0
G1 X3.77 Y-3.3 E0
G1 X1.46 Y-5.61 E0
;
; Post-layer lift
G1 X1.46 Y-5.61 Z1.25 E0 F210
; END_LAYER_OBJECT z=0.75
; BEGIN_LAYER_OBJECT z=1.000
; fan %*255
M106 S255
;
; 'Perimeter Path', 0.2 [feed mm/s], 10.0 [head mm/s]
G1 X4.96 Y6.81 Z1.25 E0 F30000
G1 X4.96 Y6.81 Z1 E0 F210
; 'Destring Prime'
G1 E1.25 F900
G1 X5.05 Y7.24 E0.0077 F600
G1 X-5.13 Y7.23 E0.18
G1 X-5.23 Y7.18 E0.0022
G1 X-5.23 Y-7.18 E0.2538
G1 X-5.14 Y-7.23 E0.0019
G1 X5.14 Y-7.23 E0.1818
G1 X5.23 Y-7.18 E0.0019
G1 X5.23 Y6.98 E0.2503
G1 X4.84 Y6.93 E0.007
;
; 'Loop Path', 0.5 [feed mm/s], 26.2 [head mm/s]
G1 X4.54 Y6.73 E0 F30000
G1 X-4.61 Y6.73 E0.1618 F1575
G1 X-4.73 Y6.67 E0.0024
G1 X-4.73 Y-6.67 E0.2359
G1 X-4.61 Y-6.73 E0.0024
G1 X4.61 Y-6.73 E0.1629
G1 X4.73 Y-6.67 E0.0023
G1 X4.73 Y6.47 E0.2325
;
; 'Perimeter Path', 0.2 [feed mm/s], 10.0 [head mm/s]
G1 X3.84 Y2.23 E0 F30000
G1 X3.49 Y2.34 E0.0066 F600
G1 X0.12 Y4.29 E0.0688
G1 X-0.18 Y4.25 E0.0053
G1 X-3.68 Y2.23 E0.0714
G1 X-3.77 Y1.93 E0.0056
G1 X-3.77 Y-1.91 E0.0678
G1 X-3.72 Y-2.14 E0.0043
G1 X-3.58 Y-2.29 E0.0036
G1 X-0.12 Y-4.29 E0.0707
G1 X0.18 Y-4.25 E0.0052
G1 X3.68 Y-2.23 E0.0714
G1 X3.77 Y-1.93 E0.0057
G1 X3.77 Y1.9 E0.0676
G1 X3.74 Y2.07 E0.003
G1 X3.71 Y2.42 E0.0063
;
; 'Loop Path', 0.5 [feed mm/s], 26.2 [head mm/s]
G1 X3.85 Y2.71 E0 F30000
G1 X0.41 Y4.7 E0.0702 F1575
G1 X0.13 Y4.79 E0.0053
G1 X-0.4 Y4.71 E0.0094
G1 X-4.03 Y2.6 E0.0743
G1 X-4.27 Y2.03 E0.0109
G1 X-4.27 Y-2.01 E0.0714
G1 X-4.16 Y-2.39 E0.0071
G1 X-3.94 Y-2.66 E0.0062
G1 X-0.41 Y-4.7 E0.0719
G1 X-0.13 Y-4.79 E0.0053
G1 X0.4 Y-4.71 E0.0094
G1 X4.03 Y-2.6 E0.0743
G1 X4.27 Y-2.03 E0.0109
G1 X4.27 Y2.02 E0.0716
G1 X4.12 Y2.44 E0.0078
;
; 'Loop Path', 0.8 [feed mm/s], 42.5 [head mm/s]
G1 X4.22 Y3.26 E0 F30000
G1 X4.22 Y6.17 E0.0514 F2550
G1 X4.1 Y6.23 E0.0024
G1 X-4.1 Y6.23 E0.1451
G1 X-4.22 Y6.17 E0.0024
G1 X-4.22 Y3.19 E0.0526
G1 X-4.08 Y3.16 E0.0027
G1 X-0.65 Y5.14 E0.0699
G1 X-0.28 Y5.26 E0.007
G1 X0.29 Y5.28 E0.0101
G1 X0.65 Y5.14 E0.0068
G1 X3.93 Y3.24 E0.067
;
; 'Loop Path', 0.8 [feed mm/s], 42.5 [head mm/s]
G1 X4.05 Y-3.17 E0 F30000
G1 X0.69 Y-5.11 E0.0686 F2550
G1 X0.02 Y-5.3 E0.0124
G1 X-0.65 Y-5.14 E0.0121
G1 X-4.08 Y-3.16 E0.07
G1 X-4.22 Y-3.2 E0.0027
G1 X-4.22 Y-6.17 E0.0526
G1 X-4.1 Y-6.23 E0.0023
G1 X4.1 Y-6.23 E0.1452
G1 X4.22 Y-6.17 E0.0023
G1 X4.22 Y-3.4 E0.0491
; fan off
M107
;
; 'Solid Path', 0.7 [feed mm/s], 37.5 [head mm/s]
G1 X3.32 Y-4.03 E0 F30000
G1 X3.77 Y-4.48 E0.0112 F2250
G1 X3.77 Y-5.19 E0.0126
G1 X2.93 Y-4.34 E0.0211
G1 X2.48 Y-4.6 E0.0092
G1 X3.65 Y-5.78 E0.0294
G1 X2.95 Y-5.78 E0.0125
G1 X2.03 Y-4.86 E0.0229
G1 X1.58 Y-5.12 E0.0091
G1 X2.24 Y-5.78 E0.0165
G1 X1.53 Y-5.78 E0.0125
G1 X1.16 Y-5.4 E0.0094
;
; 'Solid Path', 0.7 [feed mm/s], 37.5 [head mm/s]
G1 X-0.61 Y-5.76 E0 F30000
G1 X-1 Y-5.37 E0.0097 F2250
G1 X-1.22 Y-5.85 E0.0094
G1 X-2.53 Y-4.54 E0.0327
G1 X-3.71 Y-4.07 E0.0226
G1 X-2 Y-5.78 E0.0428
G1 X-2.71 Y-5.78 E0.0125
G1 X-3.77 Y-4.71 E0.0265
G1 X-3.75 Y-5.44 E0.0129
G1 X-3.39 Y-5.8 E0.009
;
; 'Destring/Wipe/Jump Path', 0.0 [feed mm/s], 52.5 [head mm/s]
; 'Destring Suck'
G1 E-1.25 F900
G1 X-3.75 Y-5.44 E0 F3150
G1 X-3.77 Y-4.71 E0
G1 X-2.71 Y-5.78 E0
G1 X-2 Y-5.78 E0
G1 X-3.71 Y-4.07 E0
G1 X-2.53 Y-4.54 E0
G1 X-1.22 Y-5.85 E0
G1 X-1 Y-5.37 E0
G1 X-0.66 Y-5.71 E0
;
; 'Solid Path', 0.7 [feed mm/s], 37.5 [head mm/s]
G1 X-3.38 Y4.08 E0 F30000
; 'Destring Prime'
G1 E1.25 F900
G1 X-3.77 Y4.48 E0.0099 F2250
G1 X-3.77 Y5.19 E0.0125
G1 X-2.93 Y4.34 E0.0212
G1 X-2.48 Y4.6 E0.0091
G1 X-3.65 Y5.78 E0.0294
G1 X-2.95 Y5.78 E0.0125
G1 X-2.03 Y4.86 E0.0229
G1 X-1.58 Y5.12 E0.0092
G1 X-2.24 Y5.78 E0.0164
G1 X-1.53 Y5.78 E0.0125
G1 X-1.1 Y5.35 E0.0108
G1 X-0.87 Y5.81 E0.0093
G1 X-0.66 Y5.61 E0.0052
;
; 'Solid Path', 0.7 [feed mm/s], 37.5 [head mm/s]
G1 X0.94 Y5.42 E0 F30000
G1 X0.55 Y5.81 E0.0097 F2250
G1 X1.27 Y5.8 E0.0127
G1 X2.53 Y4.54 E0.0314
G1 X3.71 Y4.07 E0.0226
G1 X2 Y5.78 E0.0427
G1 X2.71 Y5.78 E0.0125
G1 X3.77 Y4.71 E0.0266
G1 X3.75 Y5.44 E0.0129
G1 X3.34 Y5.85 E0.0103
;
; 'Destring/Wipe/Jump Path', 0.0 [feed mm/s], 52.5 [head mm/s]
; 'Destring Suck'
G1 E-1.25 F900
G1 X3.75 Y5.44 E0 F3150
G1 X3.77 Y4.71 E0
G1 X2.71 Y5.78 E0
G1 X2 Y5.78 E0
G1 X3.71 Y4.07 E0
G1 X2.53 Y4.54 E0
G1 X1.27 Y5.8 E0
G1 X0.55 Y5.81 E0
G1 X0.76 Y5.6 E0
;
; Post-layer lift
G1 X0.76 Y5.6 Z1.5 E0 F210
; END_LAYER_OBJECT z=1.00
; BEGIN_LAYER_OBJECT z=1.250
; fan %*255
M106 S255
;
; 'Perimeter Path', 0.2 [feed mm/s], 10.0 [head mm/s]
G1 X4.86 Y6.84 Z1.5 E0 F30000
G1 X4.86 Y6.84 Z1.25 E0 F210
; 'Destring Prime'
G1 E1.25 F900
G1 X4.92 Y7.23 E0.0071 F600
G1 X-5.01 Y7.24 E0.1755
G1 X-5.16 Y7.13 E0.0033
G1 X-5.16 Y-7.14 E0.2522
G1 X-4.99 Y-7.24 E0.0035
G1 X-4.94 Y-7.23 E0.001
G1 X5.11 Y-7.23 E0.1777
G1 X5.16 Y-7.1 E0.0024
G1 X5.16 Y7.05 E0.2502
G1 X4.74 Y6.96 E0.0077
;
; 'Loop Path', 0.5 [feed mm/s], 26.2 [head mm/s]
G1 X4.41 Y6.73 E0 F30000
G1 X-4.61 Y6.73 E0.1595 F1575
G1 X-4.66 Y6.62 E0.002
G1 X-4.66 Y-6.62 E0.2342
G1 X-4.61 Y-6.73 E0.0021
G1 X4.61 Y-6.73 E0.1631
G1 X4.66 Y-6.62 E0.002
G1 X4.66 Y6.55 E0.2328
;
; 'Perimeter Path', 0.2 [feed mm/s], 10.0 [head mm/s]
G1 X3.84 Y2.23 E0 F30000
G1 X3.49 Y2.34 E0.0067 F600
G1 X0.12 Y4.29 E0.0688
G1 X-0.18 Y4.25 E0.0053
G1 X-3.68 Y2.23 E0.0714
G1 X-3.77 Y1.93 E0.0056
G1 X-3.77 Y-1.91 E0.0678
G1 X-3.72 Y-2.14 E0.0043
G1 X-3.58 Y-2.29 E0.0036
G1 X-0.12 Y-4.29 E0.0706
G1 X0.18 Y-4.25 E0.0053
G1 X3.68 Y-2.23 E0.0714
G1 X3.77 Y-1.93 E0.0056
G1 X3.77 Y1.9 E0.0677
G1 X3.74 Y2.07 E0.003
G1 X3.71 Y2.42 E0.0062
;
; 'Loop Path', 0.5 [feed mm/s], 26.2 [head mm/s]
G1 X3.85 Y2.71 E0 F30000
G1 X0.41 Y4.7 E0.0703 F1575
G1 X0.13 Y4.79 E0.0053
G1 X-0.4 Y4.71 E0.0093
G1 X-4.03 Y2.6 E0.0743
G1 X-4.27 Y2.03 E0.0109
G1 X-4.27 Y-2.01 E0.0715
G1 X-4.16 Y-2.39 E0.0071
G1 X-3.94 Y-2.66 E0.0061
G1 X-0.41 Y-4.7 E0.072
G1 X-0.13 Y-4.79 E0.0053
G1 X0.4 Y-4.71 E0.0094
G1 X4.03 Y-2.6 E0.0743
G1 X4.27 Y-2.03 E0.0109
G1 X4.27 Y2.02 E0.0715
G1 X4.12 Y2.44 E0.0079
;
; 'Loop Path', 0.8 [feed mm/s], 42.5 [head mm/s]
G1 X4.16 Y3.33 E0 F30000
G1 X4.15 Y6.1 E0.049 F2550
G1 X4.1 Y6.23 E0.0024
G1 X-4.1 Y6.23 E0.1452
G1 X-4.15 Y6.1 E0.0024
G1 X-4.15 Y3.27 E0.05
G1 X-4.11 Y3.14 E0.0024
G1 X-0.69 Y5.11 E0.0697
G1 X-0.02 Y5.3 E0.0124
G1 X0.65 Y5.14 E0.0121
G1 X3.94 Y3.24 E0.0672
;
; 'Loop Path', 0.8 [feed mm/s], 42.5 [head mm/s]
G1 X3.94 Y-3.24 E0 F30000
G1 X0.69 Y-5.11 E0.0662 F2550
G1 X0.02 Y-5.3 E0.0124
G1 X-0.65 Y-5.14 E0.0121
G1 X-4.11 Y-3.14 E0.0706
G1 X-4.15 Y-3.27 E0.0024
G1 X-4.15 Y-6.1 E0.0501
G1 X-4.1 Y-6.23 E0.0024
G1 X4.1 Y-6.23 E0.1451
G1 X4.15 Y-6.1 E0.0024
G1 X4.16 Y-3.33 E0.049
;
; 'Destring/Wipe/Jump Path', 0.0 [feed mm/s], 52.5 [head mm/s]
; 'Destring Suck'
G1 E-1.25 F900
G1 X3.94 Y-3.24 E0 F3150
G1 X0.69 Y-5.11 E0
G1 X0.02 Y-5.3 E0
G1 X-0.65 Y-5.14 E0
G1 X-4.11 Y-3.14 E0
G1 X-4.15 Y-3.27 E0
G1 X-4.15 Y-4.01 E0
; fan off
M107
;
; 'Stacked Sparse Infill Path', 0.9 [feed mm/s], 52.5 [head mm/s]
G1 X-0.4 Y5.97 E0 F30000
; 'Destring Prime'
G1 E1.25 F900
G1 X-0.71 Y5.66 E0.0078 F3150
G1 X-3.54 Y5.66 E0.05
G1 X-3.67 Y5.52 E0.0035
G1 X-3.21 Y5.98 E0.0115
G1 X-2.74 Y5.98 E0.0082
G1 X-1.77 Y5.8 E0.0176
G1 X-1.19 Y5.13 E0.0156
G1 X-0.58 Y5.42 E0.012
G1 X0.04 Y5.52 E0.0111
G1 X0.71 Y5.37 E0.0123
G1 X1.44 Y4.98 E0.0146
G1 X2.12 Y5.66 E0.017
G1 X3.81 Y5.66 E0.0299
;
; 'Stacked Sparse Infill Path', 0.9 [feed mm/s], 52.5 [head mm/s]
G1 X3.81 Y-5.66 E0 F30000
G1 X2.12 Y-5.66 E0.0299 F3150
G1 X1.8 Y-5.98 E0.0081
G1 X0.82 Y-5.95 E0.0173
G1 X0.35 Y-5.59 E0.0105
G1 X-0.51 Y-5.46 E0.0153
G1 X-0.71 Y-5.66 E0.005
G1 X-3.54 Y-5.66 E0.05
G1 X-3.67 Y-5.79 E0.0035
;
; 'Destring/Wipe/Jump Path', 0.0 [feed mm/s], 52.5 [head mm/s]
; 'Destring Suck'
G1 E-1.25 F900
G1 X-3.54 Y-5.66 E0 F3150
G1 X-0.71 Y-5.66 E0
G1 X-0.51 Y-5.46 E0
G1 X0.35 Y-5.59 E0
G1 X0.82 Y-5.95 E0
G1 X1.8 Y-5.98 E0
G1 X2.12 Y-5.66 E0
G1 X3.81 Y-5.66 E0
;
; Post-layer lift
G1 X3.81 Y-5.66 Z1.75 E0 F210
; END_LAYER_OBJECT z=1.25
; BEGIN_LAYER_OBJECT z=1.500
; fan %*255
M106 S255
;
; 'Perimeter Path', 0.2 [feed mm/s], 10.0 [head mm/s]
G1 X4.65 Y6.83 Z1.75 E0 F30000
G1 X4.65 Y6.83 Z1.5 E0 F210
; 'Destring Prime'
G1 E1.25 F900
G1 X4.73 Y7.23 E0.0073 F600
G1 X-4.83 Y7.23 E0.169
G1 X-4.93 Y7.12 E0.0028
G1 X-4.93 Y7.05 E0.0013
G1 X-4.93 Y-7.23 E0.2524
G1 X-4.82 Y-7.23 E0.0019
G1 X4.83 Y-7.23 E0.1706
G1 X4.93 Y-7.12 E0.0028
G1 X4.93 Y-7.05 E0.0012
G1 X4.93 Y7.03 E0.2489
G1 X4.53 Y6.95 E0.0072
;
; 'Loop Path', 0.5 [feed mm/s], 26.2 [head mm/s]
G1 X4.23 Y6.73 E0 F30000
G1 X-4.42 Y6.73 E0.153 F1575
G1 X-4.42 Y2.65 E0.0722
G1 X-4.27 Y2.69 E0.0028
G1 X-4.12 Y2.6 E0.0031
G1 X-4.01 Y2.59 E0.0019
G1 X-3.83 Y2.72 E0.0039
G1 X-0.4 Y4.7 E0.07
G1 X0.04 Y4.79 E0.0081
G1 X0.4 Y4.71 E0.0064
G1 X3.83 Y2.72 E0.0701
G1 X4.01 Y2.59 E0.004
G1 X4.12 Y2.6 E0.0018
G1 X4.27 Y2.69 E0.0031
G1 X4.42 Y2.65 E0.0029
G1 X4.42 Y6.52 E0.0686
;
; 'Loop Path', 0.8 [feed mm/s], 42.5 [head mm/s]
G1 X3.72 Y6.23 E0 F30000
G1 X-3.92 Y6.23 E0.1352 F2550
G1 X-3.92 Y3.33 E0.0512
G1 X-3.83 Y3.3 E0.0017
G1 X-0.69 Y5.11 E0.0641
G1 X-0.02 Y5.3 E0.0124
G1 X0.64 Y5.14 E0.012
G1 X3.83 Y3.3 E0.0651
G1 X3.92 Y3.33 E0.0017
G1 X3.92 Y6.02 E0.0476
;
; 'Perimeter Path', 0.2 [feed mm/s], 10.0 [head mm/s]
G1 X3.84 Y2.23 E0 F30000
G1 X3.49 Y2.34 E0.0067 F600
G1 X0.12 Y4.29 E0.0688
G1 X-0.18 Y4.25 E0.0053
G1 X-3.68 Y2.23 E0.0714
G1 X-3.77 Y1.93 E0.0056
G1 X-3.77 Y-1.91 E0.0678
G1 X-3.72 Y-2.14 E0.0042
G1 X-3.58 Y-2.29 E0.0037
G1 X-0.12 Y-4.29 E0.0706
G1 X0.18 Y-4.25 E0.0053
G1 X3.68 Y-2.23 E0.0714
G1 X3.77 Y-1.93 E0.0056
G1 X3.77 Y1.9 E0.0677
G1 X3.74 Y2.07 E0.003
G1 X3.71 Y2.42 E0.0062
;
; 'Destring/Wipe/Jump Path', 0.0 [feed mm/s], 52.5 [head mm/s]
; 'Destring Suck'
G1 E-1.25 F900
G1 X3.84 Y2.23 E0 F3150
G1 X3.49 Y2.34 E0
G1 X0.12 Y4.29 E0
G1 X-0.18 Y4.25 E0
G1 X-3.68 Y2.23 E0
G1 X-3.77 Y1.93 E0
G1 X-3.77 Y0.85 E0
;
; 'Loop Path', 0.5 [feed mm/s], 26.2 [head mm/s]
G1 X4.23 Y-2.67 E0 F30000
; 'Destring Prime'
G1 E1.25 F900
G1 X4.12 Y-2.6 E0.0024 F1575
G1 X4.01 Y-2.59 E0.0019
G1 X3.83 Y-2.72 E0.004
G1 X0.4 Y-4.7 E0.07
G1 X-0.04 Y-4.79 E0.008
G1 X-0.4 Y-4.71 E0.0065
G1 X-3.83 Y-2.72 E0.0701
G1 X-4.01 Y-2.59 E0.0039
G1 X-4.12 Y-2.6 E0.0019
G1 X-4.27 Y-2.69 E0.0031
G1 X-4.42 Y-2.65 E0.0028
G1 X-4.42 Y-6.73 E0.0722
G1 X4.42 Y-6.72 E0.1564
G1 X4.42 Y-2.84 E0.0687
;
; 'Loop Path', 0.8 [feed mm/s], 42.5 [head mm/s]
G1 X3.74 Y-3.35 E0 F30000
G1 X0.69 Y-5.11 E0.0622 F2550
G1 X0.02 Y-5.3 E0.0124
G1 X-0.64 Y-5.14 E0.0121
G1 X-3.83 Y-3.3 E0.0651
G1 X-3.92 Y-3.33 E0.0016
G1 X-3.92 Y-6.23 E0.0513
G1 X3.92 Y-6.22 E0.1386
G1 X3.92 Y-3.52 E0.0478
;
; 'Destring/Wipe/Jump Path', 0.0 [feed mm/s], 52.5 [head mm/s]
; 'Destring Suck'
G1 E-1.25 F900
G1 X3.74 Y-3.35 E0 F3150
G1 X0.69 Y-5.11 E0
G1 X0.02 Y-5.3 E0
G1 X-0.64 Y-5.14 E0
G1 X-3.83 Y-3.3 E0
G1 X-3.92 Y-3.33 E0
G1 X-3.92 Y-4.65 E0
;
; 'Crown Path', 0.7 [feed mm/s], 30.1 [head mm/s]
G1 X-4.31 Y-2.3 E0 F30000
; 'Destring Prime'
G1 E1.25 F900
G1 X-4.35 Y-1.99 E0.0072 F1804.2
G1 X-4.35 Y1.99 E0.0905
G1 X-4.31 Y2.3 E0.0071
;
; 'Destring/Wipe/Jump Path', 0.0 [feed mm/s], 52.5 [head mm/s]
; 'Destring Suck'
G1 E-1.25 F900
G1 X-4.35 Y1.99 E0 F3150
G1 X-4.35 Y-1.99 E0
G1 X-4.31 Y-2.3 E0
;
; 'Crown Path', 0.7 [feed mm/s], 30.1 [head mm/s]
G1 X4.31 Y2.3 E0 F30000
; 'Destring Prime'
G1 E1.25 F900
G1 X4.35 Y1.99 E0.0072 F1804.2
G1 X4.35 Y-1.99 E0.0905
G1 X4.31 Y-2.3 E0.0071
; fan off
M107
;
; 'Sparse Infill Path', 0.9 [feed mm/s], 52.5 [head mm/s]
G1 X3.46 Y-4.17 E0 F30000
G1 X3.66 Y-4.37 E0.0052 F3150
G1 X3.67 Y-5.19 E0.0145
G1 X3.46 Y-5.58 E0.0078
G1 X3.67 Y-5.79 E0.0053
;
; 'Sparse Infill Path', 0.9 [feed mm/s], 52.5 [head mm/s]
G1 X-3.46 Y-5.73 E0 F30000
G1 X-3.66 Y-5.53 E0.0051 F3150
G1 X-3.67 Y-4.71 E0.0145
G1 X-3.46 Y-4.32 E0.0078
G1 X-3.67 Y-4.11 E0.0053
;
; 'Destring/Wipe/Jump Path', 0.0 [feed mm/s], 52.5 [head mm/s]
; 'Destring Suck'
G1 E-1.25 F900
G1 X-3.46 Y-4.32 E0 F3150
G1 X-3.67 Y-4.71 E0
G1 X-3.66 Y-5.53 E0
G1 X-3.46 Y-5.73 E0
;
; 'Sparse Infill Path', 0.9 [feed mm/s], 52.5 [head mm/s]
G1 X-3.46 Y4.17 E0 F30000
; 'Destring Prime'
G1 E1.25 F900
G1 X-3.67 Y4.38 E0.0053 F3150
G1 X-3.67 Y4.85 E0.0084
G1 X-3.89 Y5.32 E0.0091
G1 X-3.67 Y5.79 E0.0092
G1 X-3.46 Y5.58 E0.0053
;
; 'Sparse Infill Path', 0.9 [feed mm/s], 52.5 [head mm/s]
G1 X3.46 Y5.73 E0 F30000
G1 X3.66 Y5.53 E0.0051 F3150
G1 X3.67 Y4.71 E0.0145
G1 X3.46 Y4.32 E0.0078
G1 X3.67 Y4.11 E0.0053
;
; 'Stacked Sparse Infill Path', 0.9 [feed mm/s], 52.5 [head mm/s]
G1 X3.1 Y5.66 E0 F30000
G1 X2.12 Y5.66 E0.0174 F3150
G1 X1.8 Y5.98 E0.0081
G1 X0.82 Y5.95 E0.0173
G1 X0.36 Y5.59 E0.0103
G1 X-0.5 Y5.45 E0.0155
G1 X-0.71 Y5.66 E0.0051
G1 X-3.1 Y5.66 E0.0424
;
; 'Destring/Wipe/Jump Path', 0.0 [feed mm/s], 52.5 [head mm/s]
; 'Destring Suck'
G1 E-1.25 F900
G1 X-0.71 Y5.66 E0 F3150
G1 X-0.5 Y5.45 E0
G1 X0.36 Y5.59 E0
G1 X0.82 Y5.95 E0
G1 X1.8 Y5.98 E0
G1 X2.12 Y5.66 E0
G1 X3.1 Y5.66 E0
;
; 'Stacked Sparse Infill Path', 0.9 [feed mm/s], 52.5 [head mm/s]
G1 X-0.4 Y-5.97 E0 F30000
; 'Destring Prime'
G1 E1.25 F900
G1 X-0.71 Y-5.66 E0.0078 F3150
G1 X-3.1 Y-5.66 E0.0424
G1 X-2.65 Y-5.84 E0.0086
G1 X-1.89 Y-5.76 E0.0135
G1 X-1.74 Y-5.84 E0.003
G1 X-1.18 Y-5.28 E0.014
G1 X-0.54 Y-5.51 E0.0121
G1 X0.08 Y-5.57 E0.011
G1 X0.72 Y-5.39 E0.0118
G1 X1.44 Y-4.98 E0.0146
G1 X2.12 Y-5.66 E0.0171
G1 X3.1 Y-5.66 E0.0173
;
; 'Destring/Wipe/Jump Path', 0.0 [feed mm/s], 52.5 [head mm/s]
; 'Destring Suck'
G1 E-1.25 F900
G1 X2.12 Y-5.66 E0 F3150
G1 X1.44 Y-4.98 E0
G1 X0.72 Y-5.39 E0
G1 X0.08 Y-5.57 E0
G1 X-0.54 Y-5.51 E0
G1 X-1.18 Y-5.28 E0
G1 X-1.74 Y-5.84 E0
G1 X-1.89 Y-5.76 E0
G1 X-2.65 Y-5.84 E0
G1 X-3.1 Y-5.66 E0
G1 X-0.71 Y-5.66 E0
G1 X-0.4 Y-5.97 E0
;
; Post-layer lift
G1 X-0.4 Y-5.97 Z2 E0 F210
; END_LAYER_OBJECT z=1.50
; BEGIN_LAYER_OBJECT z=1.750
; fan %*255
M106 S255
;
; 'Perimeter Path', 0.2 [feed mm/s], 10.0 [head mm/s]
G1 X4.43 Y6.8 Z2 E0 F30000
G1 X4.43 Y6.8 Z1.75 E0 F210
; 'Destring Prime'
G1 E1.25 F900
G1 X4.52 Y7.24 E0.0078 F600
G1 X-4.61 Y7.24 E0.1615
G1 X-4.7 Y7.17 E0.0019
G1 X-4.7 Y-7.17 E0.2536
G1 X-4.61 Y-7.23 E0.0018
G1 X4.61 Y-7.23 E0.1631
G1 X4.7 Y-7.17 E0.0019
G1 X4.7 Y6.97 E0.2501
G1 X4.3 Y6.93 E0.0069
;
; 'Loop Path', 0.5 [feed mm/s], 26.2 [head mm/s]
G1 X4.01 Y6.73 E0 F30000
G1 X-4.08 Y6.73 E0.143 F1575
G1 X-4.19 Y6.66 E0.0023
G1 X-4.19 Y3.07 E0.0635
G1 X-4.06 Y3.05 E0.0023
G1 X-3.75 Y2.82 E0.0069
G1 X-3.69 Y2.8 E0.0011
G1 X-0.4 Y4.7 E0.0671
G1 X0.04 Y4.79 E0.0081
G1 X0.4 Y4.71 E0.0064
G1 X3.69 Y2.8 E0.0673
G1 X3.85 Y2.88 E0.0032
G1 X4.04 Y3.05 E0.0044
G1 X4.19 Y3.07 E0.0027
G1 X4.19 Y6.47 E0.06
;
; 'Loop Path', 0.8 [feed mm/s], 42.5 [head mm/s]
G1 X3.51 Y6.23 E0 F30000
G1 X-3.57 Y6.23 E0.1253 F2550
G1 X-3.69 Y6.16 E0.0022
G1 X-3.69 Y3.38 E0.0491
G1 X-0.69 Y5.11 E0.0611
G1 X-0.02 Y5.3 E0.0125
G1 X0.65 Y5.14 E0.012
G1 X3.69 Y3.38 E0.0621
G1 X3.69 Y5.96 E0.0456
;
; 'Perimeter Path', 0.2 [feed mm/s], 10.0 [head mm/s]
G1 X3.84 Y2.23 E0 F30000
G1 X3.49 Y2.34 E0.0067 F600
G1 X0.12 Y4.29 E0.0688
G1 X-0.18 Y4.25 E0.0052
G1 X-3.68 Y2.23 E0.0714
G1 X-3.77 Y1.93 E0.0057
G1 X-3.77 Y-1.91 E0.0678
G1 X-3.72 Y-2.14 E0.0042
G1 X-3.58 Y-2.29 E0.0037
G1 X-0.12 Y-4.29 E0.0706
G1 X0.18 Y-4.25 E0.0053
G1 X3.68 Y-2.23 E0.0714
G1 X3.77 Y-1.93 E0.0056
G1 X3.77 Y1.9 E0.0676
G1 X3.74 Y2.07 E0.003
G1 X3.71 Y2.42 E0.0063
;
; 'Destring/Wipe/Jump Path', 0.0 [feed mm/s], 52.5 [head mm/s]
; 'Destring Suck'
G1 E-1.25 F900
G1 X3.84 Y2.23 E0 F3150
G1 X3.49 Y2.34 E0
G1 X0.12 Y4.29 E0
G1 X-0.18 Y4.25 E0
G1 X-3.68 Y2.23 E0
G1 X-3.77 Y1.93 E0
G1 X-3.77 Y0.85 E0
;
; 'Loop Path', 0.5 [feed mm/s], 26.2 [head mm/s]
G1 X4.01 Y-3.02 E0 F30000
; 'Destring Prime'
G1 E1.25 F900
G1 X3.7 Y-2.8 E0.0068 F1575
G1 X0.4 Y-4.7 E0.0673
G1 X-0.04 Y-4.79 E0.008
G1 X-0.4 Y-4.71 E0.0065
G1 X-3.69 Y-2.8 E0.0673
G1 X-3.85 Y-2.88 E0.0031
G1 X-4.04 Y-3.05 E0.0045
G1 X-4.19 Y-3.07 E0.0026
G1 X-4.19 Y-6.66 E0.0636
G1 X-4.08 Y-6.73 E0.0023
G1 X4.08 Y-6.73 E0.1441
G1 X4.19 Y-6.66 E0.0023
G1 X4.19 Y-3.26 E0.0603
;
; 'Loop Path', 0.8 [feed mm/s], 42.5 [head mm/s]
G1 X3.51 Y-3.48 E0 F30000
G1 X0.69 Y-5.11 E0.0576 F2550
G1 X0.02 Y-5.3 E0.0124
G1 X-0.65 Y-5.14 E0.012
G1 X-3.69 Y-3.38 E0.0621
G1 X-3.69 Y-6.16 E0.0491
G1 X-3.57 Y-6.23 E0.0023
G1 X3.57 Y-6.23 E0.1264
G1 X3.69 Y-6.16 E0.0023
G1 X3.69 Y-3.58 E0.0456
;
; 'Destring/Wipe/Jump Path', 0.0 [feed mm/s], 52.5 [head mm/s]
; 'Destring Suck'
G1 E-1.25 F900
G1 X3.51 Y-3.48 E0 F3150
G1 X0.69 Y-5.11 E0
G1 X0.02 Y-5.3 E0
G1 X-0.65 Y-5.14 E0
G1 X-3.69 Y-3.38 E0
G1 X-3.69 Y-5.23 E0
;
; 'Crown Path', 0.4 [feed mm/s], 28.0 [head mm/s]
G1 X-4.1 Y-2.56 E0 F30000
; 'Destring Prime'
G1 E1.25 F900
G1 X-4.17 Y-2.4 E0.0027 F1681
G1 X-4.23 Y-1.86 E0.0081
G1 X-4.23 Y2.01 E0.0576
G1 X-4.09 Y2.68 E0.0103
;
; 'Destring/Wipe/Jump Path', 0.0 [feed mm/s], 52.5 [head mm/s]
; 'Destring Suck'
G1 E-1.25 F900
G1 X-4.23 Y2.01 E0 F3150
G1 X-4.23 Y-1.86 E0
G1 X-4.17 Y-2.4 E0
G1 X-4.1 Y-2.56 E0
;
; 'Crown Path', 0.4 [feed mm/s], 28.0 [head mm/s]
G1 X4.09 Y2.68 E0 F30000
; 'Destring Prime'
G1 E1.25 F900
G1 X4.23 Y2.06 E0.0094 F1681
G1 X4.23 Y-1.97 E0.0602
G1 X4.09 Y-2.68 E0.0107
;
; 'Destring/Wipe/Jump Path', 0.0 [feed mm/s], 52.5 [head mm/s]
; 'Destring Suck'
G1 E-1.25 F900
G1 X4.23 Y-1.97 E0 F3150
G1 X4.23 Y2.06 E0
G1 X4.09 Y2.68 E0
; fan off
M107
;
; 'Stacked Sparse Infill Path', 0.9 [feed mm/s], 52.5 [head mm/s]
G1 X-0.4 Y5.97 E0 F30000
; 'Destring Prime'
G1 E1.25 F900
G1 X-0.71 Y5.66 E0.0078 F3150
G1 X-3.1 Y5.66 E0.0424
;
; 'Stacked Sparse Infill Path', 0.9 [feed mm/s], 52.5 [head mm/s]
G1 X1.44 Y4.97 E0 F30000
G1 X2.12 Y5.66 E0.0171 F3150
G1 X3.1 Y5.66 E0.0174
;
; 'Destring/Wipe/Jump Path', 0.0 [feed mm/s], 52.5 [head mm/s]
; 'Destring Suck'
G1 E-1.25 F900
G1 X2.12 Y5.66 E0 F3150
G1 X1.44 Y4.97 E0
;
; 'Stacked Sparse Infill Path', 0.9 [feed mm/s], 52.5 [head mm/s]
G1 X3.1 Y-5.66 E0 F30000
; 'Destring Prime'
G1 E1.25 F900
G1 X2.12 Y-5.66 E0.0173 F3150
G1 X1.8 Y-5.98 E0.0082
G1 X0.82 Y-5.95 E0.0173
G1 X0.35 Y-5.59 E0.0105
G1 X-0.51 Y-5.46 E0.0153
G1 X-0.71 Y-5.66 E0.005
G1 X-3.1 Y-5.66 E0.0423
;
; 'Destring/Wipe/Jump Path', 0.0 [feed mm/s], 52.5 [head mm/s]
; 'Destring Suck'
G1 E-1.25 F900
G1 X-0.71 Y-5.66 E0 F3150
G1 X-0.51 Y-5.46 E0
G1 X0.35 Y-5.59 E0
G1 X0.82 Y-5.95 E0
G1 X1.8 Y-5.98 E0
G1 X2.12 Y-5.66 E0
G1 X3.1 Y-5.66 E0
;
; Post-layer lift
G1 X3.1 Y-5.66 Z2.25 E0 F210
; END_LAYER_OBJECT z=1.75
; BEGIN_LAYER_OBJECT z=2.000
; fan %*255
M106 S255
;
; 'Perimeter Path', 0.2 [feed mm/s], 10.0 [head mm/s]
G1 X4.16 Y6.84 Z2.25 E0 F30000
G1 X4.16 Y6.84 Z2 E0 F210
; 'Destring Prime'
G1 E1.25 F900
G1 X4.21 Y7.23 E0.0071 F600
G1 X-4.41 Y7.23 E0.1523
G1 X-4.46 Y7.11 E0.0024
G1 X-4.46 Y-7.14 E0.252
G1 X-4.28 Y-7.24 E0.0035
G1 X-4.23 Y-7.23 E0.001
G1 X4.41 Y-7.23 E0.1527
G1 X4.46 Y-7.11 E0.0024
G1 X4.46 Y7.05 E0.2503
G1 X4.03 Y6.96 E0.0076
;
; 'Loop Path', 0.5 [feed mm/s], 26.2 [head mm/s]
G1 X3.7 Y6.73 E0 F30000
G1 X-3.9 Y6.73 E0.1345 F1575
G1 X-3.95 Y6.65 E0.0016
G1 X-3.95 Y3.07 E0.0634
G1 X-3.83 Y2.89 E0.0038
G1 X-3.69 Y2.8 E0.0029
G1 X-0.4 Y4.7 E0.0672
G1 X0.04 Y4.79 E0.008
G1 X0.4 Y4.71 E0.0065
G1 X3.69 Y2.8 E0.0672
G1 X3.83 Y2.89 E0.003
G1 X3.95 Y3.07 E0.0038
G1 X3.95 Y6.55 E0.0615
;
; 'Loop Path', 0.8 [feed mm/s], 42.5 [head mm/s]
G1 X3.2 Y6.23 E0 F30000
G1 X-3.4 Y6.23 E0.1167 F2550
G1 X-3.45 Y6.1 E0.0025
G1 X-3.45 Y3.62 E0.0437
G1 X-3.38 Y3.56 E0.0017
G1 X-0.69 Y5.11 E0.0549
G1 X-0.02 Y5.3 E0.0123
G1 X0.59 Y5.17 E0.011
G1 X3.38 Y3.56 E0.0571
G1 X3.45 Y3.62 E0.0017
G1 X3.45 Y6.04 E0.0427
;
; 'Perimeter Path', 0.2 [feed mm/s], 10.0 [head mm/s]
G1 X3.84 Y2.23 E0 F30000
G1 X3.49 Y2.34 E0.0067 F600
G1 X0.12 Y4.29 E0.0687
G1 X-0.18 Y4.25 E0.0053
G1 X-3.68 Y2.23 E0.0714
G1 X-3.77 Y1.93 E0.0057
G1 X-3.77 Y-1.91 E0.0678
G1 X-3.72 Y-2.14 E0.0042
G1 X-3.58 Y-2.29 E0.0037
G1 X-0.12 Y-4.29 E0.0706
G1 X0.18 Y-4.25 E0.0053
G1 X3.68 Y-2.23 E0.0714
G1 X3.77 Y-1.93 E0.0056
G1 X3.77 Y1.9 E0.0676
G1 X3.74 Y2.07 E0.003
G1 X3.71 Y2.42 E0.0063
;
; 'Destring/Wipe/Jump Path', 0.0 [feed mm/s], 52.5 [head mm/s]
; 'Destring Suck'
G1 E-1.25 F900
G1 X3.84 Y2.23 E0 F3150
G1 X3.49 Y2.34 E0
G1 X0.12 Y4.29 E0
G1 X-0.18 Y4.25 E0
G1 X-3.68 Y2.23 E0
G1 X-3.77 Y1.93 E0
G1 X-3.77 Y0.85 E0
;
; 'Loop Path', 0.5 [feed mm/s], 26.2 [head mm/s]
G1 X3.66 Y-2.82 E0 F30000
; 'Destring Prime'
G1 E1.25 F900
G1 X0.4 Y-4.7 E0.0665 F1575
G1 X-0.04 Y-4.79 E0.0081
G1 X-0.4 Y-4.71 E0.0064
G1 X-3.69 Y-2.8 E0.0673
G1 X-3.83 Y-2.89 E0.0029
G1 X-3.95 Y-3.07 E0.0038
G1 X-3.95 Y-6.65 E0.0635
G1 X-3.9 Y-6.73 E0.0016
G1 X3.9 Y-6.73 E0.1379
G1 X3.95 Y-6.65 E0.0016
G1 X3.95 Y-3.07 E0.0634
G1 X3.94 Y-3.05 E0.0004
;
; 'Loop Path', 0.8 [feed mm/s], 42.5 [head mm/s]
G1 X3.29 Y-3.61 E0 F30000
G1 X0.69 Y-5.11 E0.053 F2550
G1 X0.02 Y-5.3 E0.0124
G1 X-0.59 Y-5.17 E0.011
G1 X-3.38 Y-3.56 E0.0571
G1 X-3.45 Y-3.62 E0.0016
G1 X-3.45 Y-6.1 E0.0438
G1 X-3.4 Y-6.23 E0.0024
G1 X3.4 Y-6.23 E0.1202
G1 X3.45 Y-6.1 E0.0024
G1 X3.45 Y-3.82 E0.0404
;
; 'Destring/Wipe/Jump Path', 0.0 [feed mm/s], 52.5 [head mm/s]
; 'Destring Suck'
G1 E-1.25 F900
G1 X3.29 Y-3.61 E0 F3150
G1 X0.69 Y-5.11 E0
G1 X0.02 Y-5.3 E0
G1 X-0.59 Y-5.17 E0
G1 X-3.38 Y-3.56 E0
G1 X-3.45 Y-3.62 E0
G1 X-3.45 Y-5.98 E0
;
; 'Crown Path', 0.2 [feed mm/s], 25.6 [head mm/s]
G1 X-4 Y-2.47 E0 F30000
; 'Destring Prime'
G1 E1.25 F900
G1 X-4.11 Y-2 E0.0043 F1536.9
G1 X-4.11 Y2 E0.0353
G1 X-4 Y2.58 E0.0053
;
; 'Destring/Wipe/Jump Path', 0.0 [feed mm/s], 52.5 [head mm/s]
; 'Destring Suck'
G1 E-1.25 F900
G1 X-4.11 Y2 E0 F3150
G1 X-4.11 Y-2 E0
G1 X-4 Y-2.47 E0
;
; 'Crown Path', 0.2 [feed mm/s], 25.6 [head mm/s]
G1 X4 Y2.58 E0 F30000
; 'Destring Prime'
G1 E1.25 F900
G1 X4.11 Y2 E0.0052 F1536.9
G1 X4.11 Y-2 E0.0354
G1 X4 Y-2.58 E0.0053
;
; 'Destring/Wipe/Jump Path', 0.0 [feed mm/s], 52.5 [head mm/s]
; 'Destring Suck'
G1 E-1.25 F900
G1 X4.11 Y-2 E0 F3150
G1 X4.11 Y2 E0
G1 X4 Y2.58 E0
; fan off
M107
;
; 'Stacked Sparse Infill Path', 0.9 [feed mm/s], 52.5 [head mm/s]
G1 X3.1 Y5.66 E0 F30000
; 'Destring Prime'
G1 E1.25 F900
G1 X2.12 Y5.66 E0.0174 F3150
G1 X1.8 Y5.98 E0.0081
G1 X0.82 Y5.95 E0.0173
G1 X0.36 Y5.59 E0.0102
G1 X-0.5 Y5.45 E0.0155
G1 X-0.71 Y5.66 E0.0052
G1 X-3.1 Y5.66 E0.0424
;
; 'Destring/Wipe/Jump Path', 0.0 [feed mm/s], 52.5 [head mm/s]
; 'Destring Suck'
G1 E-1.25 F900
G1 X-0.71 Y5.66 E0 F3150
G1 X-0.5 Y5.45 E0
G1 X0.36 Y5.59 E0
G1 X0.82 Y5.95 E0
G1 X1.8 Y5.98 E0
G1 X2.12 Y5.66 E0
G1 X3.1 Y5.66 E0
;
; 'Stacked Sparse Infill Path', 0.9 [feed mm/s], 52.5 [head mm/s]
G1 X-3.1 Y-5.66 E0 F30000
; 'Destring Prime'
G1 E1.25 F900
G1 X-0.71 Y-5.66 E0.0423 F3150
G1 X-0.4 Y-5.97 E0.0078
;
; 'Stacked Sparse Infill Path', 0.9 [feed mm/s], 52.5 [head mm/s]
G1 X1.44 Y-4.97 E0 F30000
G1 X2.12 Y-5.66 E0.0171 F3150
G1 X3.1 Y-5.66 E0.0174
;
; 'Destring/Wipe/Jump Path', 0.0 [feed mm/s], 52.5 [head mm/s]
; 'Destring Suck'
G1 E-1.25 F900
G1 X2.12 Y-5.66 E0 F3150
G1 X1.44 Y-4.97 E0
;
; Post-layer lift
G1 X1.44 Y-4.97 Z2.5 E0 F210
; END_LAYER_OBJECT z=2.00
; BEGIN_LAYER_OBJECT z=2.250
; fan %*255
M106 S255
;
; 'Perimeter Path', 0.2 [feed mm/s], 10.0 [head mm/s]
G1 X3.95 Y6.83 Z2.5 E0 F30000
G1 X3.95 Y6.83 Z2.25 E0 F210
; 'Destring Prime'
G1 E1.25 F900
G1 X4.03 Y7.23 E0.0072 F600
G1 X-4.13 Y7.23 E0.1443
G1 X-4.23 Y7.11 E0.0029
G1 X-4.23 Y7.05 E0.0009
G1 X-4.23 Y-7.23 E0.2527
G1 X4.13 Y-7.23 E0.1477
G1 X4.23 Y-7.11 E0.0029
G1 X4.23 Y-7.05 E0.001
G1 X4.23 Y7.03 E0.2491
G1 X3.83 Y6.96 E0.0072
;
; 'Loop Path', 0.5 [feed mm/s], 26.2 [head mm/s]
G1 X3.52 Y6.73 E0 F30000
G1 X-3.72 Y6.73 E0.1281 F1575
G1 X-3.72 Y2.82 E0.0691
G1 X-3.69 Y2.8 E0.0006
G1 X-0.4 Y4.7 E0.0671
G1 X0.04 Y4.79 E0.0081
G1 X0.4 Y4.71 E0.0064
G1 X3.69 Y2.8 E0.0672
G1 X3.72 Y2.82 E0.0007
G1 X3.72 Y6.53 E0.0656
;
; 'Loop Path', 0.8 [feed mm/s], 42.5 [head mm/s]
G1 X3.02 Y6.23 E0 F30000
G1 X-3.22 Y6.23 E0.1103 F2550
G1 X-3.22 Y3.68 E0.045
G1 X-3.16 Y3.69 E0.001
G1 X-0.69 Y5.11 E0.0504
G1 X-0.02 Y5.3 E0.0124
G1 X0.59 Y5.17 E0.011
G1 X3.16 Y3.69 E0.0525
G1 X3.22 Y3.68 E0.001
G1 X3.22 Y6.03 E0.0416
;
; 'Perimeter Path', 0.2 [feed mm/s], 10.0 [head mm/s]
G1 X3.84 Y2.23 E0 F30000
G1 X3.49 Y2.34 E0.0066 F600
G1 X0.12 Y4.29 E0.0688
G1 X-0.18 Y4.25 E0.0053
G1 X-3.68 Y2.23 E0.0714
G1 X-3.77 Y1.93 E0.0056
G1 X-3.77 Y-1.91 E0.0678
G1 X-3.72 Y-2.14 E0.0043
G1 X-3.58 Y-2.29 E0.0036
G1 X-0.12 Y-4.29 E0.0706
G1 X0.18 Y-4.25 E0.0053
G1 X3.68 Y-2.23 E0.0714
G1 X3.77 Y-1.93 E0.0056
G1 X3.77 Y1.9 E0.0677
G1 X3.74 Y2.07 E0.003
G1 X3.71 Y2.42 E0.0062
;
; 'Destring/Wipe/Jump Path', 0.0 [feed mm/s], 52.5 [head mm/s]
; 'Destring Suck'
G1 E-1.25 F900
G1 X3.84 Y2.23 E0 F3150
G1 X3.49 Y2.34 E0
G1 X0.12 Y4.29 E0
G1 X-0.18 Y4.25 E0
G1 X-3.68 Y2.23 E0
G1 X-3.77 Y1.93 E0
G1 X-3.77 Y0.85 E0
;
; 'Loop Path', 0.5 [feed mm/s], 26.2 [head mm/s]
G1 X3.55 Y-2.88 E0 F30000
; 'Destring Prime'
G1 E1.25 F900
G1 X0.4 Y-4.7 E0.0643 F1575
G1 X-0.04 Y-4.79 E0.0081
G1 X-0.4 Y-4.71 E0.0064
G1 X-3.69 Y-2.8 E0.0672
G1 X-3.72 Y-2.82 E0.0007
G1 X-3.72 Y-6.73 E0.069
G1 X3.72 Y-6.73 E0.1316
G1 X3.72 Y-3.02 E0.0656
;
; 'Loop Path', 0.8 [feed mm/s], 42.5 [head mm/s]
G1 X3.04 Y-3.76 E0 F30000
G1 X0.69 Y-5.11 E0.0479 F2550
G1 X0.02 Y-5.3 E0.0124
G1 X-0.59 Y-5.17 E0.011
G1 X-3.16 Y-3.69 E0.0525
G1 X-3.22 Y-3.68 E0.001
G1 X-3.22 Y-6.23 E0.0451
G1 X3.22 Y-6.23 E0.1138
G1 X3.22 Y-3.88 E0.0416
;
; 'Destring/Wipe/Jump Path', 0.0 [feed mm/s], 52.5 [head mm/s]
; 'Destring Suck'
G1 E-1.25 F900
G1 X3.04 Y-3.76 E0 F3150
G1 X0.69 Y-5.11 E0
G1 X0.02 Y-5.3 E0
G1 X-0.59 Y-5.17 E0
G1 X-3.16 Y-3.69 E0
G1 X-3.22 Y-3.68 E0
G1 X-3.22 Y-6.23 E0
G1 X-2.82 Y-6.23 E0
; fan off
M107
;
; 'Stacked Sparse Infill Path', 0.9 [feed mm/s], 52.5 [head mm/s]
G1 X-0.4 Y5.97 E0 F30000
; 'Destring Prime'
G1 E1.25 F900
G1 X-0.71 Y5.66 E0.0078 F3150
G1 X-2.4 Y5.66 E0.0298
G1 X-1.92 Y5.83 E0.009
G1 X-1.26 Y5.31 E0.0149
G1 X-0.94 Y5.36 E0.0057
G1 X-0.57 Y5.51 E0.0071
G1 X-0.01 Y5.58 E0.01
G1 X0.68 Y5.43 E0.0124
G1 X1.44 Y4.98 E0.0157
G1 X2.12 Y5.66 E0.017
G1 X2.4 Y5.66 E0.0049
;
; 'Destring/Wipe/Jump Path', 0.0 [feed mm/s], 52.5 [head mm/s]
; 'Destring Suck'
G1 E-1.25 F900
G1 X2.12 Y5.66 E0 F3150
G1 X1.44 Y4.98 E0
G1 X0.68 Y5.43 E0
G1 X-0.01 Y5.58 E0
G1 X-0.57 Y5.51 E0
G1 X-0.94 Y5.36 E0
G1 X-1.26 Y5.31 E0
G1 X-1.92 Y5.83 E0
G1 X-2.4 Y5.66 E0
G1 X-0.71 Y5.66 E0
G1 X-0.4 Y5.97 E0
;
; 'Stacked Sparse Infill Path', 0.9 [feed mm/s], 52.5 [head mm/s]
G1 X2.4 Y-5.66 E0 F30000
; 'Destring Prime'
G1 E1.25 F900
G1 X2.12 Y-5.66 E0.0048 F3150
G1 X1.8 Y-5.98 E0.0081
G1 X0.82 Y-5.95 E0.0174
G1 X0.35 Y-5.59 E0.0105
G1 X-0.51 Y-5.46 E0.0153
G1 X-0.71 Y-5.66 E0.0049
G1 X-2.4 Y-5.66 E0.0299
;
; 'Destring/Wipe/Jump Path', 0.0 [feed mm/s], 52.5 [head mm/s]
; 'Destring Suck'
G1 E-1.25 F900
G1 X-0.71 Y-5.66 E0 F3150
G1 X-0.51 Y-5.46 E0
G1 X0.35 Y-5.59 E0
G1 X0.82 Y-5.95 E0
G1 X1.8 Y-5.98 E0
G1 X2.12 Y-5.66 E0
G1 X2.4 Y-5.66 E0
;
; Post-layer lift
G1 X2.4 Y-5.66 Z2.75 E0 F210
; END_LAYER_OBJECT z=2.25
; BEGIN_LAYER_OBJECT z=2.500
; fan %*255
M106 S255
;
; 'Perimeter Path', 0.2 [feed mm/s], 10.0 [head mm/s]
G1 X3.72 Y6.81 Z2.75 E0 F30000
G1 X3.72 Y6.81 Z2.5 E0 F210
; 'Destring Prime'
G1 E1.25 F900
G1 X3.82 Y7.24 E0.0078 F600
G1 X-3.89 Y7.23 E0.1362
G1 X-3.99 Y7.17 E0.0021
G1 X-3.99 Y2.56 E0.0815
G1 X-3.7 Y2.31 E0.0069
G1 X-3.54 Y2.31 E0.0026
G1 X-0.18 Y4.25 E0.0688
G1 X0.12 Y4.29 E0.0052
G1 X3.54 Y2.31 E0.07
G1 X3.69 Y2.31 E0.0027
G1 X3.99 Y2.56 E0.0068
G1 X3.99 Y6.98 E0.0781
G1 X3.6 Y6.93 E0.007
;
; 'Loop Path', 0.5 [feed mm/s], 26.2 [head mm/s]
G1 X3.3 Y6.73 E0 F30000
G1 X-3.37 Y6.73 E0.118 F1575
G1 X-3.48 Y6.67 E0.0023
G1 X-3.48 Y3.05 E0.0639
G1 X-3.37 Y2.99 E0.0023
G1 X-0.4 Y4.7 E0.0605
G1 X0.04 Y4.79 E0.0081
G1 X0.4 Y4.71 E0.0064
G1 X3.37 Y2.99 E0.0607
G1 X3.48 Y3.06 E0.0024
G1 X3.48 Y6.47 E0.0604
;
; 'Loop Path', 0.8 [feed mm/s], 42.5 [head mm/s]
G1 X2.8 Y6.23 E0 F30000
G1 X-2.87 Y6.23 E0.1002 F2550
G1 X-2.98 Y6.16 E0.0023
G1 X-2.98 Y3.91 E0.0399
G1 X-2.83 Y3.88 E0.0027
G1 X-0.69 Y5.11 E0.0437
G1 X-0.02 Y5.3 E0.0123
G1 X0.59 Y5.17 E0.011
G1 X2.83 Y3.88 E0.0459
G1 X2.98 Y3.91 E0.0027
G1 X2.98 Y5.97 E0.0364
;
; 'Destring/Wipe/Jump Path', 0.0 [feed mm/s], 52.5 [head mm/s]
; 'Destring Suck'
G1 E-1.25 F900
G1 X2.8 Y6.23 E0 F3150
G1 X-2.87 Y6.23 E0
G1 X-2.98 Y6.16 E0
G1 X-2.98 Y3.91 E0
G1 X-2.83 Y3.88 E0
G1 X-1.28 Y4.77 E0
;
; 'Perimeter Path', 0.2 [feed mm/s], 10.0 [head mm/s]
G1 X3.68 Y-2.81 E0 F30000
; 'Destring Prime'
G1 E1.25 F900
G1 X3.83 Y-2.44 E0.0071 F600
G1 X3.7 Y-2.31 E0.0033
G1 X3.54 Y-2.31 E0.0027
G1 X0.18 Y-4.25 E0.0687
G1 X-0.12 Y-4.29 E0.0053
G1 X-3.54 Y-2.31 E0.07
G1 X-3.69 Y-2.31 E0.0026
G1 X-3.99 Y-2.56 E0.0069
G1 X-3.99 Y-7.17 E0.0815
G1 X-3.9 Y-7.23 E0.0019
G1 X3.9 Y-7.23 E0.1381
G1 X3.99 Y-7.17 E0.0019
G1 X3.99 Y-2.75 E0.0782
G1 X3.59 Y-2.6 E0.0075
;
; 'Loop Path', 0.5 [feed mm/s], 26.2 [head mm/s]
G1 X3.21 Y-3.08 E0 F30000
G1 X0.4 Y-4.7 E0.0574 F1575
G1 X-0.04 Y-4.79 E0.0081
G1 X-0.4 Y-4.71 E0.0064
G1 X-3.37 Y-2.99 E0.0606
G1 X-3.48 Y-3.06 E0.0024
G1 X-3.48 Y-6.67 E0.0639
G1 X-3.37 Y-6.73 E0.0023
G1 X3.37 Y-6.73 E0.1191
G1 X3.48 Y-6.67 E0.0024
G1 X3.48 Y-3.13 E0.0626
;
; 'Loop Path', 0.8 [feed mm/s], 42.5 [head mm/s]
G1 X2.8 Y-3.9 E0 F30000
G1 X0.69 Y-5.11 E0.043 F2550
G1 X0.02 Y-5.3 E0.0123
G1 X-0.59 Y-5.17 E0.011
G1 X-2.83 Y-3.88 E0.0459
G1 X-2.98 Y-3.91 E0.0027
G1 X-2.98 Y-6.16 E0.0399
G1 X-2.87 Y-6.23 E0.0023
G1 X2.87 Y-6.23 E0.1014
G1 X2.98 Y-6.16 E0.0023
G1 X2.98 Y-4.11 E0.0364
;
; 'Destring/Wipe/Jump Path', 0.0 [feed mm/s], 52.5 [head mm/s]
; 'Destring Suck'
G1 E-1.25 F900
G1 X2.8 Y-3.9 E0 F3150
G1 X0.69 Y-5.11 E0
G1 X0.02 Y-5.3 E0
G1 X-0.59 Y-5.17 E0
G1 X-2.83 Y-3.88 E0
G1 X-2.98 Y-3.91 E0
G1 X-2.98 Y-6.16 E0
G1 X-2.87 Y-6.23 E0
G1 X-1.75 Y-6.23 E0
;
; 'Crown Path', 0.5 [feed mm/s], 18.6 [head mm/s]
G1 X-3.85 Y-2.12 E0 F30000
; 'Destring Prime'
G1 E1.25 F900
G1 X-3.88 Y-1.89 E0.0057 F1113.3
G1 X-3.88 Y1.89 E0.0948
G1 X-3.85 Y2.12 E0.0058
;
; 'Destring/Wipe/Jump Path', 0.0 [feed mm/s], 52.5 [head mm/s]
; 'Destring Suck'
G1 E-1.25 F900
G1 X-3.88 Y1.89 E0 F3150
G1 X-3.88 Y-1.89 E0
G1 X-3.85 Y-2.12 E0
;
; 'Crown Path', 0.5 [feed mm/s], 18.6 [head mm/s]
G1 X3.85 Y2.12 E0 F30000
; 'Destring Prime'
G1 E1.25 F900
G1 X3.88 Y1.89 E0.0057 F1113.3
G1 X3.88 Y-1.89 E0.0948
G1 X3.85 Y-2.12 E0.0058
;
; 'Destring/Wipe/Jump Path', 0.0 [feed mm/s], 52.5 [head mm/s]
; 'Destring Suck'
G1 E-1.25 F900
G1 X3.88 Y-1.89 E0 F3150
G1 X3.88 Y1.89 E0
G1 X3.85 Y2.12 E0
; fan off
M107
;
; 'Stacked Sparse Infill Path', 0.9 [feed mm/s], 52.5 [head mm/s]
G1 X2.4 Y5.66 E0 F30000
; 'Destring Prime'
G1 E1.25 F900
G1 X2.12 Y5.66 E0.0048 F3150
G1 X1.8 Y5.98 E0.0081
G1 X0.82 Y5.95 E0.0174
G1 X0.36 Y5.59 E0.0102
G1 X-0.5 Y5.45 E0.0155
G1 X-0.71 Y5.66 E0.0052
G1 X-2.4 Y5.66 E0.0298
;
; 'Destring/Wipe/Jump Path', 0.0 [feed mm/s], 52.5 [head mm/s]
; 'Destring Suck'
G1 E-1.25 F900
G1 X-0.71 Y5.66 E0 F3150
G1 X-0.5 Y5.45 E0
G1 X0.36 Y5.59 E0
G1 X0.82 Y5.95 E0
G1 X1.8 Y5.98 E0
G1 X2.12 Y5.66 E0
G1 X2.4 Y5.66 E0
;
; 'Stacked Sparse Infill Path', 0.9 [feed mm/s], 52.5 [head mm/s]
G1 X-2.4 Y-5.66 E0 F30000
; 'Destring Prime'
G1 E1.25 F900
G1 X-0.71 Y-5.66 E0.0299 F3150
G1 X-0.4 Y-5.97 E0.0078
;
; 'Stacked Sparse Infill Path', 0.9 [feed mm/s], 52.5 [head mm/s]
G1 X1.44 Y-4.97 E0 F30000
G1 X2.12 Y-5.66 E0.0171 F3150
G1 X2.4 Y-5.66 E0.0048
;
; 'Destring/Wipe/Jump Path', 0.0 [feed mm/s], 52.5 [head mm/s]
; 'Destring Suck'
G1 E-1.25 F900
G1 X2.12 Y-5.66 E0 F3150
G1 X1.44 Y-4.97 E0
;
; Post-layer lift
G1 X1.44 Y-4.97 Z3 E0 F210
; END_LAYER_OBJECT z=2.50
; BEGIN_LAYER_OBJECT z=2.750
; fan %*255
M106 S255
;
; 'Perimeter Path', 0.2 [feed mm/s], 10.0 [head mm/s]
G1 X3.45 Y6.84 Z3 E0 F30000
G1 X3.45 Y6.84 Z2.75 E0 F210
; 'Destring Prime'
G1 E1.25 F900
G1 X3.5 Y7.23 E0.007 F600
G1 X-3.7 Y7.23 E0.1274
G1 X-3.75 Y7.11 E0.0024
G1 X-3.75 Y2.79 E0.0764
G1 X-3.5 Y2.51 E0.0066
G1 X-3.32 Y2.43 E0.0034
G1 X-0.18 Y4.25 E0.0643
G1 X0.12 Y4.29 E0.0052
G1 X3.32 Y2.43 E0.0655
G1 X3.5 Y2.52 E0.0035
G1 X3.75 Y2.79 E0.0065
G1 X3.75 Y7.05 E0.0754
G1 X3.33 Y6.96 E0.0077
;
; 'Loop Path', 0.5 [feed mm/s], 26.2 [head mm/s]
G1 X3 Y6.73 E0 F30000
G1 X-3.19 Y6.73 E0.1094 F1575
G1 X-3.25 Y6.65 E0.0017
G1 X-3.25 Y3.11 E0.0625
G1 X-3.13 Y3.13 E0.0021
G1 X-0.4 Y4.7 E0.0557
G1 X0.04 Y4.79 E0.0081
G1 X0.4 Y4.71 E0.0065
G1 X3.13 Y3.13 E0.0558
G1 X3.25 Y3.11 E0.0021
G1 X3.25 Y6.55 E0.0608
;
; 'Loop Path', 0.8 [feed mm/s], 42.5 [head mm/s]
G1 X2.49 Y6.23 E0 F30000
G1 X-2.69 Y6.23 E0.0916 F2550
G1 X-2.75 Y6.11 E0.0024
G1 X-2.75 Y3.97 E0.0378
G1 X-2.71 Y3.95 E0.0008
G1 X-0.69 Y5.11 E0.0412
G1 X-0.02 Y5.3 E0.0123
G1 X0.59 Y5.17 E0.011
G1 X2.71 Y3.95 E0.0433
G1 X2.75 Y3.97 E0.0008
G1 X2.75 Y6.04 E0.0367
;
; 'Destring/Wipe/Jump Path', 0.0 [feed mm/s], 52.5 [head mm/s]
; 'Destring Suck'
G1 E-1.25 F900
G1 X2.49 Y6.23 E0 F3150
G1 X-2.69 Y6.23 E0
G1 X-2.75 Y6.11 E0
G1 X-2.75 Y3.97 E0
G1 X-2.71 Y3.95 E0
G1 X-0.69 Y5.11 E0
G1 X-0.53 Y5.16 E0
;
; 'Perimeter Path', 0.2 [feed mm/s], 10.0 [head mm/s]
G1 X3.51 Y-2.9 E0 F30000
; 'Destring Prime'
G1 E1.25 F900
G1 X3.43 Y-2.48 E0.0076 F600
G1 X3.32 Y-2.43 E0.0021
G1 X0.18 Y-4.25 E0.0642
G1 X-0.12 Y-4.29 E0.0053
G1 X-3.32 Y-2.43 E0.0655
G1 X-3.5 Y-2.52 E0.0035
G1 X-3.75 Y-2.79 E0.0065
G1 X-3.75 Y-7.14 E0.077
G1 X-3.58 Y-7.24 E0.0036
G1 X-3.52 Y-7.23 E0.0009
G1 X3.7 Y-7.23 E0.1277
G1 X3.75 Y-7.11 E0.0024
G1 X3.75 Y-2.79 E0.0764
G1 X3.71 Y-2.75 E0.001
G1 X3.31 Y-2.75 E0.0071
;
; 'Loop Path', 0.5 [feed mm/s], 26.2 [head mm/s]
G1 X3.06 Y-3.16 E0 F30000
G1 X0.4 Y-4.7 E0.0544 F1575
G1 X-0.04 Y-4.79 E0.0081
G1 X-0.4 Y-4.71 E0.0064
G1 X-3.13 Y-3.13 E0.0558
G1 X-3.25 Y-3.11 E0.0021
G1 X-3.25 Y-6.65 E0.0626
G1 X-3.19 Y-6.73 E0.0017
G1 X3.19 Y-6.73 E0.1129
G1 X3.25 Y-6.65 E0.0017
G1 X3.25 Y-3.31 E0.059
;
; 'Loop Path', 0.8 [feed mm/s], 42.5 [head mm/s]
G1 X2.58 Y-4.02 E0 F30000
G1 X0.69 Y-5.11 E0.0385 F2550
G1 X0.02 Y-5.3 E0.0124
G1 X-0.59 Y-5.17 E0.011
G1 X-2.71 Y-3.95 E0.0433
G1 X-2.75 Y-3.97 E0.0008
G1 X-2.75 Y-6.11 E0.0378
G1 X-2.69 Y-6.23 E0.0024
G1 X2.69 Y-6.23 E0.0951
G1 X2.75 Y-6.11 E0.0023
G1 X2.75 Y-4.16 E0.0344
;
; 'Destring/Wipe/Jump Path', 0.0 [feed mm/s], 52.5 [head mm/s]
; 'Destring Suck'
G1 E-1.25 F900
G1 X2.58 Y-4.02 E0 F3150
G1 X0.69 Y-5.11 E0
G1 X0.02 Y-5.3 E0
G1 X-0.59 Y-5.17 E0
G1 X-2.71 Y-3.95 E0
G1 X-2.75 Y-3.97 E0
G1 X-2.75 Y-6.11 E0
G1 X-2.69 Y-6.23 E0
G1 X-0.95 Y-6.23 E0
;
; 'Crown Path', 0.2 [feed mm/s], 10.0 [head mm/s]
G1 X-3.65 Y-2.3 E0 F30000
; 'Destring Prime'
G1 E1.25 F900
G1 X-3.76 Y-1.98 E0.0057 F600
G1 X-3.76 Y1.89 E0.0648
G1 X-3.73 Y2.13 E0.004
G1 X-3.65 Y2.3 E0.0032
;
; 'Destring/Wipe/Jump Path', 0.0 [feed mm/s], 52.5 [head mm/s]
; 'Destring Suck'
G1 E-1.25 F900
G1 X-3.73 Y2.13 E0 F3150
G1 X-3.76 Y1.89 E0
G1 X-3.76 Y-1.98 E0
G1 X-3.65 Y-2.3 E0
;
; 'Crown Path', 0.2 [feed mm/s], 10.0 [head mm/s]
G1 X3.65 Y2.3 E0 F30000
; 'Destring Prime'
G1 E1.25 F900
G1 X3.76 Y1.98 E0.0057 F600
G1 X3.76 Y-1.89 E0.0649
G1 X3.73 Y-2.13 E0.0039
G1 X3.65 Y-2.3 E0.0032
;
; 'Destring/Wipe/Jump Path', 0.0 [feed mm/s], 52.5 [head mm/s]
; 'Destring Suck'
G1 E-1.25 F900
G1 X3.73 Y-2.13 E0 F3150
G1 X3.76 Y-1.89 E0
G1 X3.76 Y1.98 E0
G1 X3.65 Y2.3 E0
; fan off
M107
;
; 'Stacked Sparse Infill Path', 0.9 [feed mm/s], 52.5 [head mm/s]
G1 X-0.4 Y5.97 E0 F30000
; 'Destring Prime'
G1 E1.25 F900
G1 X-0.71 Y5.66 E0.0078 F3150
G1 X-2.4 Y5.66 E0.0299
;
; 'Stacked Sparse Infill Path', 0.9 [feed mm/s], 52.5 [head mm/s]
G1 X1.44 Y4.97 E0 F30000
G1 X2.12 Y5.66 E0.0171 F3150
G1 X2.4 Y5.66 E0.0049
;
; 'Destring/Wipe/Jump Path', 0.0 [feed mm/s], 52.5 [head mm/s]
; 'Destring Suck'
G1 E-1.25 F900
G1 X2.12 Y5.66 E0 F3150
G1 X1.44 Y4.97 E0
;
; 'Stacked Sparse Infill Path', 0.9 [feed mm/s], 52.5 [head mm/s]
G1 X2.4 Y-5.66 E0 F30000
; 'Destring Prime'
G1 E1.25 F900
G1 X2.12 Y-5.66 E0.0048 F3150
G1 X1.8 Y-5.98 E0.0081
G1 X0.82 Y-5.95 E0.0174
G1 X0.35 Y-5.59 E0.0105
G1 X-0.51 Y-5.46 E0.0153
G1 X-0.71 Y-5.66 E0.0049
G1 X-2.4 Y-5.66 E0.0299
;
; 'Destring/Wipe/Jump Path', 0.0 [feed mm/s], 52.5 [head mm/s]
; 'Destring Suck'
G1 E-1.25 F900
G1 X-0.71 Y-5.66 E0 F3150
G1 X-0.51 Y-5.46 E0
G1 X0.35 Y-5.59 E0
G1 X0.82 Y-5.95 E0
G1 X1.8 Y-5.98 E0
G1 X2.12 Y-5.66 E0
G1 X2.4 Y-5.66 E0
;
; Post-layer lift
G1 X2.4 Y-5.66 Z3.25 E0 F210
; END_LAYER_OBJECT z=2.75
; BEGIN_LAYER_OBJECT z=3.000
; fan %*255
M106 S255
;
; 'Perimeter Path', 0.2 [feed mm/s], 10.0 [head mm/s]
G1 X3.25 Y6.83 Z3.25 E0 F30000
G1 X3.25 Y6.83 Z3 E0 F210
; 'Destring Prime'
G1 E1.25 F900
G1 X3.33 Y7.23 E0.0072 F600
G1 X-3.52 Y7.23 E0.1211
G1 X-3.52 Y2.84 E0.0777
G1 X-3.4 Y2.79 E0.0024
G1 X-3.31 Y2.69 E0.0024
G1 X-3.1 Y2.56 E0.0044
G1 X-0.18 Y4.25 E0.0596
G1 X0.12 Y4.29 E0.0053
G1 X3.1 Y2.56 E0.0609
G1 X3.52 Y2.85 E0.0091
G1 X3.52 Y7.04 E0.074
G1 X3.12 Y6.96 E0.0072
;
; 'Loop Path', 0.5 [feed mm/s], 26.2 [head mm/s]
G1 X2.82 Y6.73 E0 F30000
G1 X-3.02 Y6.73 E0.1032 F1575
G1 X-3.02 Y3.35 E0.0598
G1 X-2.96 Y3.22 E0.0024
G1 X-0.4 Y4.7 E0.0523
G1 X0.04 Y4.79 E0.0081
G1 X0.4 Y4.71 E0.0064
G1 X2.96 Y3.22 E0.0524
G1 X3.02 Y3.35 E0.0024
G1 X3.02 Y6.53 E0.0564
;
; 'Loop Path', 0.8 [feed mm/s], 42.5 [head mm/s]
G1 X2.32 Y6.23 E0 F30000
G1 X-2.51 Y6.23 E0.0854 F2550
G1 X-2.52 Y4.2 E0.0358
G1 X-2.43 Y4.11 E0.0022
G1 X-0.69 Y5.11 E0.0355
G1 X-0.02 Y5.3 E0.0123
G1 X0.59 Y5.17 E0.011
G1 X2.43 Y4.11 E0.0376
G1 X2.52 Y4.2 E0.0022
G1 X2.52 Y6.03 E0.0323
;
; 'Destring/Wipe/Jump Path', 0.0 [feed mm/s], 52.5 [head mm/s]
; 'Destring Suck'
G1 E-1.25 F900
G1 X2.32 Y6.23 E0 F3150
G1 X-2.51 Y6.23 E0
G1 X-2.52 Y4.2 E0
G1 X-2.43 Y4.11 E0
G1 X-0.69 Y5.11 E0
G1 X-0.02 Y5.3 E0
G1 X0.29 Y5.23 E0
;
; 'Crown Path', 0.1 [feed mm/s], 10.0 [head mm/s]
G1 X3.48 Y2.42 E0 F30000
; 'Destring Prime'
G1 E1.25 F900
G1 X3.65 Y1.96 E0.0055 F600
G1 X3.65 Y-1.86 E0.0425
G1 X3.48 Y-2.42 E0.0065
;
; 'Destring/Wipe/Jump Path', 0.0 [feed mm/s], 52.5 [head mm/s]
; 'Destring Suck'
G1 E-1.25 F900
G1 X3.65 Y-1.86 E0 F3150
G1 X3.65 Y1.96 E0
G1 X3.48 Y2.42 E0
; fan off
M107
;
; 'Sparse Infill Path', 0.9 [feed mm/s], 52.5 [head mm/s]
G1 X-2.27 Y5.8 E0 F30000
; 'Destring Prime'
G1 E1.25 F900
G1 X-1.44 Y4.98 E0.0206 F3150
G1 X-0.51 Y5.46 E0.0186
G1 X-1.03 Y5.98 E0.0129
G1 X-0.05 Y5.91 E0.0174
G1 X0.4 Y5.97 E0.0079
G1 X1.33 Y5.04 E0.0233
G1 X1.25 Y5.1 E0.0017
G1 X1.77 Y5.94 E0.0176
G1 X1.83 Y5.94 E0.001
G1 X2.27 Y5.51 E0.0108
;
; 'Destring/Wipe/Jump Path', 0.0 [feed mm/s], 52.5 [head mm/s]
; 'Destring Suck'
G1 E-1.25 F900
G1 X1.83 Y5.94 E0 F3150
G1 X1.77 Y5.94 E0
G1 X1.25 Y5.1 E0
G1 X1.33 Y5.04 E0
G1 X0.4 Y5.97 E0
G1 X-0.05 Y5.91 E0
G1 X-1.03 Y5.98 E0
G1 X-0.51 Y5.46 E0
G1 X-1.44 Y4.98 E0
G1 X-2.27 Y5.8 E0
; fan %*255
M106 S255
;
; 'Perimeter Path', 0.2 [feed mm/s], 10.0 [head mm/s]
G1 X3.21 Y-3.1 E0 F30000
; 'Destring Prime'
G1 E1.25 F900
G1 X3.36 Y-2.74 E0.0069 F600
G1 X3.1 Y-2.56 E0.0055
G1 X0.18 Y-4.25 E0.0598
G1 X-0.12 Y-4.29 E0.0052
G1 X-3.1 Y-2.56 E0.061
G1 X-3.52 Y-2.85 E0.0091
G1 X-3.52 Y-7.23 E0.0775
G1 X3.52 Y-7.24 E0.1246
G1 X3.52 Y-3.04 E0.0742
G1 X3.13 Y-2.88 E0.0076
;
; 'Loop Path', 0.5 [feed mm/s], 26.2 [head mm/s]
G1 X2.79 Y-3.32 E0 F30000
G1 X0.4 Y-4.7 E0.0488 F1575
G1 X-0.04 Y-4.79 E0.0081
G1 X-0.4 Y-4.71 E0.0064
G1 X-2.96 Y-3.22 E0.0525
G1 X-3.02 Y-3.35 E0.0023
G1 X-3.02 Y-6.73 E0.0598
G1 X3.02 Y-6.73 E0.1067
G1 X3.02 Y-3.41 E0.0587
;
; 'Loop Path', 0.8 [feed mm/s], 42.5 [head mm/s]
G1 X2.26 Y-4.21 E0 F30000
G1 X0.69 Y-5.11 E0.032 F2550
G1 X0.02 Y-5.3 E0.0124
G1 X-0.59 Y-5.17 E0.0109
G1 X-2.43 Y-4.11 E0.0377
G1 X-2.52 Y-4.2 E0.0022
G1 X-2.51 Y-6.23 E0.0358
G1 X2.51 Y-6.23 E0.0889
G1 X2.52 Y-4.27 E0.0345
;
; 'Destring/Wipe/Jump Path', 0.0 [feed mm/s], 52.5 [head mm/s]
; 'Destring Suck'
G1 E-1.25 F900
G1 X2.26 Y-4.21 E0 F3150
G1 X0.69 Y-5.11 E0
G1 X0.02 Y-5.3 E0
G1 X-0.59 Y-5.17 E0
G1 X-2.43 Y-4.11 E0
G1 X-2.52 Y-4.2 E0
G1 X-2.51 Y-6.23 E0
G1 X0.08 Y-6.23 E0
;
; 'Crown Path', 0.1 [feed mm/s], 10.0 [head mm/s]
G1 X-3.48 Y-2.42 E0 F30000
; 'Destring Prime'
G1 E1.25 F900
G1 X-3.65 Y-1.93 E0.0058 F600
G1 X-3.65 Y1.93 E0.0429
G1 X-3.48 Y2.42 E0.0058
;
; 'Destring/Wipe/Jump Path', 0.0 [feed mm/s], 52.5 [head mm/s]
; 'Destring Suck'
G1 E-1.25 F900
G1 X-3.65 Y1.93 E0 F3150
G1 X-3.65 Y-1.93 E0
G1 X-3.48 Y-2.42 E0
; fan off
M107
;
; 'Sparse Infill Path', 0.9 [feed mm/s], 52.5 [head mm/s]
G1 X-2.27 Y-5.51 E0 F30000
; 'Destring Prime'
G1 E1.25 F900
G1 X-1.83 Y-5.94 E0.0108 F3150
G1 X-1.77 Y-5.94 E0.0011
G1 X-1.25 Y-5.1 E0.0175
G1 X-1.33 Y-5.04 E0.0017
G1 X-0.4 Y-5.97 E0.0233
G1 X-0.1 Y-5.88 E0.0055
G1 X0.16 Y-5.58 E0.0071
G1 X0.5 Y-5.45 E0.0063
G1 X1.02 Y-5.97 E0.013
G1 X1.14 Y-5.15 E0.0146
G1 X1.44 Y-4.97 E0.0061
G1 X2.27 Y-5.8 E0.0207
;
; 'Destring/Wipe/Jump Path', 0.0 [feed mm/s], 52.5 [head mm/s]
; 'Destring Suck'
G1 E-1.25 F900
G1 X1.44 Y-4.97 E0 F3150
G1 X1.14 Y-5.15 E0
G1 X1.02 Y-5.97 E0
G1 X0.5 Y-5.45 E0
G1 X0.16 Y-5.58 E0
G1 X-0.1 Y-5.88 E0
G1 X-0.4 Y-5.97 E0
G1 X-1.33 Y-5.04 E0
G1 X-1.25 Y-5.1 E0
G1 X-1.77 Y-5.94 E0
G1 X-1.83 Y-5.94 E0
G1 X-2.27 Y-5.51 E0
;
; Post-layer lift
G1 X-2.27 Y-5.51 Z3.5 E0 F210
; END_LAYER_OBJECT z=3.00
; BEGIN_LAYER_OBJECT z=3.250
; fan %*255
M106 S255
;
; 'Perimeter Path', 0.2 [feed mm/s], 10.0 [head mm/s]
G1 X3.02 Y6.81 Z3.5 E0 F30000
G1 X3.02 Y6.81 Z3.25 E0 F210
; 'Destring Prime'
G1 E1.25 F900
G1 X3.11 Y7.24 E0.0077 F600
G1 X-3.18 Y7.23 E0.1113
G1 X-3.29 Y7.18 E0.0021
G1 X-3.29 Y3.08 E0.0725
G1 X-3.05 Y2.8 E0.0065
G1 X-2.87 Y2.69 E0.0036
G1 X-0.18 Y4.25 E0.055
G1 X0.12 Y4.29 E0.0053
G1 X2.88 Y2.69 E0.0563
G1 X3.11 Y2.86 E0.0051
G1 X3.29 Y3.08 E0.005
G1 X3.29 Y6.98 E0.0689
G1 X2.89 Y6.93 E0.007
;
; 'Loop Path', 0.5 [feed mm/s], 26.2 [head mm/s]
G1 X2.6 Y6.73 E0 F30000
G1 X-2.66 Y6.73 E0.093 F1575
G1 X-2.78 Y6.67 E0.0024
G1 X-2.78 Y3.41 E0.0577
G1 X-2.68 Y3.39 E0.0018
G1 X-0.4 Y4.7 E0.0465
G1 X0.04 Y4.79 E0.0081
G1 X0.4 Y4.71 E0.0064
G1 X2.68 Y3.39 E0.0467
G1 X2.78 Y3.41 E0.0018
G1 X2.78 Y6.47 E0.0542
;
; 'Loop Path', 0.8 [feed mm/s], 42.5 [head mm/s]
G1 X2.1 Y6.23 E0 F30000
G1 X-2.16 Y6.23 E0.0753 F2550
G1 X-2.28 Y6.17 E0.0023
G1 X-2.28 Y4.26 E0.0337
G1 X-2.15 Y4.27 E0.0023
G1 X-0.56 Y5.18 E0.0324
G1 X0.01 Y5.29 E0.0104
G1 X0.59 Y5.17 E0.0103
G1 X2.15 Y4.27 E0.032
G1 X2.28 Y4.26 E0.0023
G1 X2.28 Y5.97 E0.0302
;
; 'Crown Path', 0.1 [feed mm/s], 10.0 [head mm/s]
G1 X3.19 Y2.56 E0 F30000
G1 X3.39 Y2.33 E0.0027 F600
G1 X3.55 Y1.81 E0.0048
G1 X3.55 Y-1.8 E0.032
G1 X3.38 Y-2.33 E0.0049
G1 X3.19 Y-2.56 E0.0026
;
; 'Destring/Wipe/Jump Path', 0.0 [feed mm/s], 52.5 [head mm/s]
; 'Destring Suck'
G1 E-1.25 F900
G1 X3.38 Y-2.33 E0 F3150
G1 X3.55 Y-1.8 E0
G1 X3.55 Y1.81 E0
G1 X3.39 Y2.33 E0
G1 X3.19 Y2.56 E0
; fan off
M107
;
; 'Sparse Infill Path', 0.9 [feed mm/s], 52.5 [head mm/s]
G1 X-0.4 Y5.97 E0 F30000
; 'Destring Prime'
G1 E1.25 F900
G1 X-1.31 Y5.05 E0.023 F3150
G1 X-0.54 Y5.44 E0.0152
G1 X0.05 Y5.52 E0.0106
G1 X0.51 Y5.46 E0.0082
G1 X1.02 Y5.97 E0.0127
G1 X1.35 Y5.98 E0.006
G1 X1.84 Y5.8 E0.009
G1 X2.03 Y5.56 E0.0054
G1 X1.45 Y4.99 E0.0145
;
; 'Destring/Wipe/Jump Path', 0.0 [feed mm/s], 52.5 [head mm/s]
; 'Destring Suck'
G1 E-1.25 F900
G1 X2.03 Y5.56 E0 F3150
G1 X1.84 Y5.8 E0
G1 X1.35 Y5.98 E0
G1 X1.02 Y5.97 E0
G1 X0.51 Y5.46 E0
G1 X0.05 Y5.52 E0
G1 X-0.54 Y5.44 E0
G1 X-1.31 Y5.05 E0
G1 X-0.4 Y5.97 E0
; fan %*255
M106 S255
;
; 'Perimeter Path', 0.2 [feed mm/s], 10.0 [head mm/s]
G1 X3.03 Y-3.18 E0 F30000
; 'Destring Prime'
G1 E1.25 F900
G1 X2.98 Y-2.76 E0.0075 F600
G1 X2.87 Y-2.69 E0.0021
G1 X0.18 Y-4.25 E0.0551
G1 X-0.12 Y-4.29 E0.0052
G1 X-2.88 Y-2.69 E0.0564
G1 X-3.11 Y-2.86 E0.005
G1 X-3.29 Y-3.08 E0.0051
G1 X-3.29 Y-7.18 E0.0724
G1 X-3.2 Y-7.23 E0.0019
G1 X3.2 Y-7.23 E0.113
G1 X3.29 Y-7.18 E0.0019
G1 X3.29 Y-3.08 E0.0725
G1 X3.25 Y-3.04 E0.001
G1 X2.85 Y-3.02 E0.0071
;
; 'Loop Path', 0.5 [feed mm/s], 26.2 [head mm/s]
G1 X2.6 Y-3.43 E0 F30000
G1 X0.4 Y-4.7 E0.0449 F1575
G1 X-0.04 Y-4.79 E0.0081
G1 X-0.4 Y-4.71 E0.0064
G1 X-2.68 Y-3.39 E0.0466
G1 X-2.78 Y-3.41 E0.0018
G1 X-2.78 Y-6.67 E0.0578
G1 X-2.66 Y-6.73 E0.0023
G1 X2.66 Y-6.73 E0.0942
G1 X2.78 Y-6.67 E0.0023
G1 X2.78 Y-3.6 E0.0542
;
; 'Loop Path', 0.8 [feed mm/s], 42.5 [head mm/s]
G1 X2.09 Y-4.31 E0 F30000
G1 X0.56 Y-5.18 E0.0312 F2550
G1 X-0.01 Y-5.29 E0.0104
G1 X-0.59 Y-5.17 E0.0104
G1 X-2.15 Y-4.27 E0.0319
G1 X-2.28 Y-4.26 E0.0023
G1 X-2.28 Y-6.17 E0.0337
G1 X-2.16 Y-6.23 E0.0024
G1 X2.16 Y-6.23 E0.0763
G1 X2.28 Y-6.17 E0.0024
G1 X2.28 Y-4.46 E0.0302
;
; 'Destring/Wipe/Jump Path', 0.0 [feed mm/s], 52.5 [head mm/s]
; 'Destring Suck'
G1 E-1.25 F900
G1 X2.09 Y-4.31 E0 F3150
G1 X0.56 Y-5.18 E0
G1 X-0.01 Y-5.29 E0
G1 X-0.59 Y-5.17 E0
G1 X-2.15 Y-4.27 E0
G1 X-2.28 Y-4.26 E0
G1 X-2.28 Y-6.17 E0
G1 X-2.16 Y-6.23 E0
G1 X0.93 Y-6.23 E0
;
; 'Crown Path', 0.1 [feed mm/s], 10.0 [head mm/s]
G1 X-3.19 Y-2.56 E0 F30000
; 'Destring Prime'
G1 E1.25 F900
G1 X-3.39 Y-2.33 E0.0027 F600
G1 X-3.55 Y-1.81 E0.0049
G1 X-3.53 Y1.94 E0.0331
G1 X-3.38 Y2.34 E0.0037
G1 X-3.19 Y2.56 E0.0026
;
; 'Destring/Wipe/Jump Path', 0.0 [feed mm/s], 52.5 [head mm/s]
; 'Destring Suck'
G1 E-1.25 F900
G1 X-3.38 Y2.34 E0 F3150
G1 X-3.53 Y1.94 E0
G1 X-3.55 Y-1.81 E0
G1 X-3.39 Y-2.33 E0
G1 X-3.19 Y-2.56 E0
; fan off
M107
;
; 'Sparse Infill Path', 0.9 [feed mm/s], 52.5 [head mm/s]
G1 X-2.03 Y-5.56 E0 F30000
; 'Destring Prime'
G1 E1.25 F900
G1 X-1.45 Y-4.99 E0.0144 F3150
G1 X-1.22 Y-5.12 E0.0048
G1 X-1.01 Y-5.96 E0.0153
G1 X-0.51 Y-5.46 E0.0126
G1 X-0.18 Y-5.59 E0.0063
G1 X0.09 Y-5.88 E0.007
G1 X0.4 Y-5.97 E0.0056
G1 X1.33 Y-5.04 E0.0233
;
; 'Destring/Wipe/Jump Path', 0.0 [feed mm/s], 52.5 [head mm/s]
; 'Destring Suck'
G1 E-1.25 F900
G1 X0.4 Y-5.97 E0 F3150
G1 X0.09 Y-5.88 E0
G1 X-0.18 Y-5.59 E0
G1 X-0.51 Y-5.46 E0
G1 X-1.01 Y-5.96 E0
G1 X-1.22 Y-5.12 E0
G1 X-1.45 Y-4.99 E0
G1 X-2.03 Y-5.56 E0
;
; Post-layer lift
G1 X-2.03 Y-5.56 Z3.75 E0 F210
; END_LAYER_OBJECT z=3.25
; BEGIN_LAYER_OBJECT z=3.500
; *** Slowing to match Min Layer Time (speed multiplier is 0.907536) ***
; fan %*255
M106 S255
;
; 'Perimeter Path', 0.2 [feed mm/s], 9.1 [head mm/s]
G1 X2.75 Y6.84 Z3.75 E0 F30000
G1 X2.75 Y6.84 Z3.5 E0 F210
; 'Destring Prime'
G1 E1.25 F900
G1 X2.8 Y7.23 E0.007 F544.5
G1 X-3 Y7.23 E0.1025
G1 X-3.05 Y7.06 E0.0031
G1 X-3.05 Y2.61 E0.0788
G1 X-2.99 Y2.62 E0.0011
G1 X-0.18 Y4.25 E0.0575
G1 X0.12 Y4.29 E0.0053
G1 X2.99 Y2.62 E0.0586
G1 X3.05 Y2.61 E0.0011
G1 X3.05 Y7.05 E0.0786
G1 X2.62 Y6.96 E0.0077
;
; 'Loop Path', 0.4 [feed mm/s], 23.8 [head mm/s]
G1 X2.29 Y6.73 E0 F30000
G1 X-2.49 Y6.73 E0.0845 F1429.4
G1 X-2.55 Y6.65 E0.0018
G1 X-2.55 Y3.46 E0.0562
G1 X-0.4 Y4.7 E0.0438
G1 X0.04 Y4.79 E0.0081
G1 X0.4 Y4.71 E0.0064
G1 X2.55 Y3.46 E0.0439
G1 X2.55 Y6.55 E0.0546
;
; 'Loop Path', 0.7 [feed mm/s], 38.6 [head mm/s]
G1 X1.79 Y6.23 E0 F30000
G1 X-1.98 Y6.23 E0.0667 F2314.2
G1 X-2.05 Y6.11 E0.0023
G1 X-2.05 Y4.49 E0.0286
G1 X-2.01 Y4.35 E0.0026
G1 X-0.56 Y5.18 E0.0295
G1 X0.01 Y5.29 E0.0104
G1 X0.59 Y5.17 E0.0104
G1 X2.01 Y4.35 E0.0291
G1 X2.05 Y4.49 E0.0026
G1 X2.05 Y6.05 E0.0274
;
; 'Sparse Infill Path', 0.8 [feed mm/s], 47.6 [head mm/s]
G1 X-1.8 Y5.33 E0 F30000
G1 X-1.6 Y5.14 E0.0048 F2858.7
G1 X-1.46 Y5.18 E0.0027
G1 X-1.09 Y5.94 E0.015
G1 X-1.02 Y5.97 E0.0013
G1 X-0.5 Y5.45 E0.013
G1 X-0.16 Y5.58 E0.0063
G1 X0.1 Y5.88 E0.0071
G1 X0.4 Y5.97 E0.0055
G1 X1.33 Y5.04 E0.0233
;
; 'Destring/Wipe/Jump Path', 0.0 [feed mm/s], 47.6 [head mm/s]
; 'Destring Suck'
G1 E-1.25 F900
G1 X0.4 Y5.97 E0 F2858.7
G1 X0.1 Y5.88 E0
G1 X-0.16 Y5.58 E0
G1 X-0.5 Y5.45 E0
G1 X-1.02 Y5.97 E0
G1 X-1.09 Y5.94 E0
G1 X-1.46 Y5.18 E0
G1 X-1.6 Y5.14 E0
G1 X-1.8 Y5.33 E0
;
; 'Perimeter Path', 0.2 [feed mm/s], 9.1 [head mm/s]
G1 X2.84 Y-3.09 E0 F30000
; 'Destring Prime'
G1 E1.25 F900
G1 X2.87 Y-2.69 E0.0071 F544.5
G1 X0.18 Y-4.25 E0.055
G1 X-0.12 Y-4.29 E0.0053
G1 X-2.99 Y-2.62 E0.0587
G1 X-3.05 Y-2.61 E0.0011
G1 X-3.05 Y-7.06 E0.0788
G1 X-2.99 Y-7.23 E0.0032
G1 X3 Y-7.24 E0.106
G1 X3.05 Y-7.06 E0.0032
G1 X3.05 Y-2.8 E0.0753
G1 X2.73 Y-3.03 E0.0069
;
; 'Loop Path', 0.4 [feed mm/s], 23.8 [head mm/s]
G1 X2.37 Y-3.56 E0 F30000
G1 X0.4 Y-4.7 E0.0402 F1429.4
G1 X-0.04 Y-4.79 E0.0081
G1 X-0.4 Y-4.71 E0.0065
G1 X-2.55 Y-3.46 E0.0438
G1 X-2.55 Y-6.65 E0.0563
G1 X-2.49 Y-6.73 E0.0018
G1 X2.49 Y-6.73 E0.0879
G1 X2.55 Y-6.65 E0.0018
G1 X2.55 Y-3.66 E0.0528
;
; 'Loop Path', 0.7 [feed mm/s], 38.6 [head mm/s]
G1 X1.84 Y-4.45 E0 F30000
G1 X0.56 Y-5.18 E0.0261 F2314.2
G1 X-0.01 Y-5.29 E0.0104
G1 X-0.59 Y-5.17 E0.0103
G1 X-2.01 Y-4.35 E0.0291
G1 X-2.05 Y-4.49 E0.0027
G1 X-2.05 Y-6.11 E0.0286
G1 X-1.98 Y-6.23 E0.0023
G1 X1.98 Y-6.23 E0.0701
G1 X2.05 Y-6.11 E0.0023
G1 X2.05 Y-4.54 E0.0277
;
; 'Sparse Infill Path', 0.8 [feed mm/s], 47.6 [head mm/s]
G1 X-0.4 Y-5.97 E0 F30000
G1 X-1.32 Y-5.05 E0.023 F2858.7
G1 X-0.56 Y-5.44 E0.0152
G1 X0.05 Y-5.52 E0.0107
G1 X0.5 Y-5.45 E0.0081
G1 X1.02 Y-5.97 E0.013
G1 X1.09 Y-5.94 E0.0013
G1 X1.46 Y-5.18 E0.015
G1 X1.6 Y-5.14 E0.0027
G1 X1.8 Y-5.33 E0.0048
;
; 'Destring/Wipe/Jump Path', 0.0 [feed mm/s], 47.6 [head mm/s]
; 'Destring Suck'
G1 E-1.25 F900
G1 X1.6 Y-5.14 E0 F2858.7
G1 X1.46 Y-5.18 E0
G1 X1.09 Y-5.94 E0
G1 X1.02 Y-5.97 E0
G1 X0.5 Y-5.45 E0
G1 X0.05 Y-5.52 E0
G1 X-0.56 Y-5.44 E0
G1 X-1.32 Y-5.05 E0
G1 X-0.4 Y-5.97 E0
;
; Post-layer lift
G1 X-0.4 Y-5.97 Z4 E0 F210
; END_LAYER_OBJECT z=3.50
; BEGIN_LAYER_OBJECT z=3.750
; *** Slowing to match Min Layer Time (speed multiplier is 0.827578) ***
;
; 'Perimeter Path', 0.1 [feed mm/s], 8.3 [head mm/s]
G1 X2.54 Y6.83 Z4 E0 F30000
G1 X2.54 Y6.83 Z3.75 E0 F210
; 'Destring Prime'
G1 E1.25 F900
G1 X2.62 Y7.23 E0.0072 F496.5
G1 X-2.82 Y7.23 E0.0961
G1 X-2.82 Y2.83 E0.0779
G1 X-2.66 Y2.82 E0.0029
G1 X-0.18 Y4.25 E0.0506
G1 X0.12 Y4.29 E0.0053
G1 X2.66 Y2.82 E0.0519
G1 X2.82 Y2.83 E0.0029
G1 X2.82 Y7.04 E0.0744
G1 X2.42 Y6.96 E0.0072
;
; 'Loop Path', 0.4 [feed mm/s], 21.7 [head mm/s]
G1 X2.11 Y6.73 E0 F30000
G1 X-2.31 Y6.73 E0.0783 F1303.4
G1 X-2.31 Y3.7 E0.0536
G1 X-2.23 Y3.65 E0.0017
G1 X-0.4 Y4.7 E0.0373
G1 X0.04 Y4.79 E0.0081
G1 X0.4 Y4.71 E0.0065
G1 X2.23 Y3.65 E0.0374
G1 X2.31 Y3.7 E0.0017
G1 X2.31 Y6.53 E0.0502
;
; 'Loop Path', 0.6 [feed mm/s], 35.2 [head mm/s]
G1 X1.61 Y6.23 E0 F30000
G1 X-1.81 Y6.23 E0.0604 F2110.3
G1 X-1.81 Y4.55 E0.0296
G1 X-1.7 Y4.53 E0.002
G1 X-0.56 Y5.18 E0.0233
G1 X0.01 Y5.29 E0.0103
G1 X0.59 Y5.17 E0.0104
G1 X1.7 Y4.53 E0.0227
G1 X1.81 Y4.55 E0.002
G1 X1.81 Y6.03 E0.0261
;
; 'Solid Path', 0.5 [feed mm/s], 31.0 [head mm/s]
G1 X-1.38 Y5.69 E0 F30000
G1 X-1.37 Y5.7 E0.0004 F1862.1
G1 X-1 Y5.36 E0.0089
G1 X-0.6 Y5.76 E0.0099
G1 X0.61 Y5.56 E0.0217
G1 X0.81 Y5.76 E0.0049
G1 X1.31 Y5.55 E0.0096
G1 X1.44 Y5.68 E0.0033
;
; 'Destring/Wipe/Jump Path', 0.0 [feed mm/s], 43.4 [head mm/s]
; 'Destring Suck'
G1 E-1.25 F900
G1 X1.31 Y5.55 E0 F2606.9
G1 X0.81 Y5.76 E0
G1 X0.61 Y5.56 E0
G1 X-0.6 Y5.76 E0
G1 X-1 Y5.36 E0
G1 X-1.37 Y5.7 E0
G1 X-1.38 Y5.69 E0
;
; 'Perimeter Path', 0.1 [feed mm/s], 8.3 [head mm/s]
G1 X2.61 Y-3.27 E0 F30000
; 'Destring Prime'
G1 E1.25 F900
G1 X2.63 Y-2.83 E0.0079 F496.5
G1 X0.18 Y-4.25 E0.0501
G1 X-0.12 Y-4.29 E0.0053
G1 X-2.66 Y-2.82 E0.0519
G1 X-2.82 Y-2.83 E0.0029
G1 X-2.82 Y-7.23 E0.0778
G1 X2.72 Y-7.23 E0.0979
G1 X2.82 Y-7.1 E0.003
G1 X2.82 Y-3.03 E0.072
G1 X2.5 Y-3.22 E0.0066
;
; 'Loop Path', 0.4 [feed mm/s], 21.7 [head mm/s]
G1 X2.15 Y-3.7 E0 F30000
G1 X0.4 Y-4.7 E0.0356 F1303.4
G1 X-0.04 Y-4.79 E0.008
G1 X-0.4 Y-4.71 E0.0065
G1 X-2.23 Y-3.65 E0.0374
G1 X-2.31 Y-3.7 E0.0017
G1 X-2.31 Y-6.73 E0.0536
G1 X2.31 Y-6.73 E0.0818
G1 X2.31 Y-3.89 E0.0502
;
; 'Loop Path', 0.6 [feed mm/s], 35.2 [head mm/s]
G1 X1.63 Y-4.57 E0 F30000
G1 X0.56 Y-5.18 E0.0217 F2110.3
G1 X-0.01 Y-5.29 E0.0104
G1 X-0.59 Y-5.17 E0.0103
G1 X-1.7 Y-4.53 E0.0228
G1 X-1.81 Y-4.55 E0.002
G1 X-1.81 Y-6.23 E0.0296
G1 X1.81 Y-6.23 E0.064
G1 X1.81 Y-4.75 E0.0261
;
; 'Solid Path', 0.5 [feed mm/s], 31.0 [head mm/s]
G1 X-1.38 Y-5.63 E0 F30000
G1 X-1.25 Y-5.49 E0.0033 F1862.1
G1 X-0.86 Y-5.81 E0.0089
G1 X-0.66 Y-5.61 E0.005
G1 X0.55 Y-5.81 E0.0217
G1 X0.95 Y-5.42 E0.0099
G1 X1.42 Y-5.65 E0.0094
G1 X1.44 Y-5.63 E0.0004
;
; 'Destring/Wipe/Jump Path', 0.0 [feed mm/s], 43.4 [head mm/s]
; 'Destring Suck'
G1 E-1.25 F900
G1 X1.42 Y-5.65 E0 F2606.9
G1 X0.95 Y-5.42 E0
G1 X0.55 Y-5.81 E0
G1 X-0.66 Y-5.61 E0
G1 X-0.86 Y-5.81 E0
G1 X-1.25 Y-5.49 E0
G1 X-1.38 Y-5.63 E0
;
; Post-layer lift
G1 X-1.38 Y-5.63 Z4.25 E0 F210
; END_LAYER_OBJECT z=3.75
; BEGIN_LAYER_OBJECT z=4.000
; *** Slowing to match Min Layer Time (speed multiplier is 0.829287) ***
;
; 'Perimeter Path', 0.1 [feed mm/s], 8.3 [head mm/s]
G1 X2.31 Y6.81 Z4.25 E0 F30000
G1 X2.31 Y6.81 Z4 E0 F210
; 'Destring Prime'
G1 E1.25 F900
G1 X2.4 Y7.24 E0.0077 F497.6
G1 X-2.49 Y7.23 E0.0865
G1 X-2.58 Y7.18 E0.0019
G1 X-2.58 Y2.9 E0.0757
G1 X-2.54 Y2.89 E0.0009
G1 X-0.18 Y4.25 E0.0481
G1 X0.12 Y4.29 E0.0053
G1 X2.54 Y2.89 E0.0494
G1 X2.58 Y2.9 E0.0009
G1 X2.58 Y6.98 E0.0722
G1 X2.19 Y6.94 E0.007
;
; 'Loop Path', 0.4 [feed mm/s], 21.8 [head mm/s]
G1 X1.89 Y6.73 E0 F30000
G1 X-1.95 Y6.73 E0.068 F1306.1
G1 X-2.08 Y6.67 E0.0024
G1 X-2.08 Y3.76 E0.0516
G1 X-2.01 Y3.77 E0.0013
G1 X-0.44 Y4.68 E0.032
G1 X0.13 Y4.79 E0.0102
G1 X0.41 Y4.7 E0.0053
G1 X2.01 Y3.77 E0.0326
G1 X2.08 Y3.76 E0.0013
G1 X2.08 Y6.48 E0.0481
;
; 'Loop Path', 0.6 [feed mm/s], 35.2 [head mm/s]
G1 X1.39 Y6.23 E0 F30000
G1 X-1.45 Y6.23 E0.0503 F2114.7
G1 X-1.58 Y6.17 E0.0023
G1 X-1.58 Y5.14 E0.0183
G1 X-1.47 Y5.11 E0.0019
G1 X-1.15 Y4.86 E0.0072
G1 X-0.47 Y5.22 E0.0137
G1 X0.14 Y5.29 E0.0108
G1 X0.56 Y5.19 E0.0077
G1 X1.15 Y4.85 E0.012
G1 X1.47 Y5.11 E0.0073
G1 X1.58 Y5.14 E0.002
G1 X1.58 Y5.98 E0.0148
;
; 'Crown Path', 0.7 [feed mm/s], 27.2 [head mm/s]
G1 X2.24 Y3.48 E0 F30000
G1 X2.05 Y3.71 E0.0079 F1631.7
G1 X1.98 Y3.93 E0.006
G1 X1.8 Y4.15 E0.0078
G1 X1.72 Y4.37 E0.0061
G1 X1.6 Y4.51 E0.0048
G1 X1.53 Y4.71 E0.0057
;
; 'Crown Path', 0.7 [feed mm/s], 27.2 [head mm/s]
G1 X-1.53 Y4.71 E0 F30000
G1 X-1.6 Y4.51 E0.0057 F1631.7
G1 X-1.72 Y4.37 E0.0049
G1 X-1.8 Y4.15 E0.0061
G1 X-1.98 Y3.93 E0.0078
G1 X-2.05 Y3.71 E0.0059
G1 X-2.24 Y3.48 E0.008
;
; 'Destring/Wipe/Jump Path', 0.0 [feed mm/s], 43.5 [head mm/s]
; 'Destring Suck'
G1 E-1.25 F900
G1 X-2.05 Y3.71 E0 F2612.3
G1 X-1.98 Y3.93 E0
G1 X-1.8 Y4.15 E0
G1 X-1.72 Y4.37 E0
G1 X-1.6 Y4.51 E0
G1 X-1.53 Y4.71 E0
;
; 'Perimeter Path', 0.1 [feed mm/s], 8.3 [head mm/s]
G1 X2.37 Y-3.37 E0 F30000
; 'Destring Prime'
G1 E1.25 F900
G1 X2.41 Y-2.96 E0.0073 F497.6
G1 X0.18 Y-4.25 E0.0455
G1 X-0.12 Y-4.29 E0.0053
G1 X-2.54 Y-2.89 E0.0494
G1 X-2.58 Y-2.9 E0.0009
G1 X-2.58 Y-7.18 E0.0757
G1 X-2.49 Y-7.23 E0.0019
G1 X2.49 Y-7.23 E0.088
G1 X2.58 Y-7.18 E0.0019
G1 X2.58 Y-3.09 E0.0723
G1 X2.26 Y-3.31 E0.0068
;
; 'Loop Path', 0.4 [feed mm/s], 21.8 [head mm/s]
G1 X1.9 Y-3.84 E0 F30000
G1 X0.4 Y-4.7 E0.0305 F1306.1
G1 X-0.04 Y-4.79 E0.0081
G1 X-0.4 Y-4.71 E0.0064
G1 X-2.01 Y-3.77 E0.0329
G1 X-2.08 Y-3.76 E0.0013
G1 X-2.08 Y-6.67 E0.0515
G1 X-1.95 Y-6.73 E0.0024
G1 X1.95 Y-6.73 E0.0691
G1 X2.08 Y-6.67 E0.0024
G1 X2.08 Y-3.95 E0.0481
;
; 'Loop Path', 0.6 [feed mm/s], 35.2 [head mm/s]
G1 X1.41 Y-5.06 E0 F30000
G1 X1.15 Y-4.86 E0.0057 F2114.7
G1 X0.47 Y-5.22 E0.0137
G1 X-0.14 Y-5.29 E0.0108
G1 X-0.56 Y-5.19 E0.0076
G1 X-1.15 Y-4.85 E0.0121
G1 X-1.47 Y-5.11 E0.0073
G1 X-1.58 Y-5.14 E0.002
G1 X-1.58 Y-6.17 E0.0182
G1 X-1.45 Y-6.23 E0.0024
G1 X1.45 Y-6.23 E0.0514
G1 X1.58 Y-6.17 E0.0024
G1 X1.58 Y-5.34 E0.0147
;
; 'Crown Path', 0.7 [feed mm/s], 27.2 [head mm/s]
G1 X-1.53 Y-4.71 E0 F30000
G1 X-1.6 Y-4.51 E0.0058 F1631.7
G1 X-1.72 Y-4.37 E0.0048
G1 X-1.8 Y-4.15 E0.0061
G1 X-1.98 Y-3.93 E0.0078
G1 X-2.05 Y-3.71 E0.006
G1 X-2.24 Y-3.48 E0.0079
;
; 'Destring/Wipe/Jump Path', 0.0 [feed mm/s], 43.5 [head mm/s]
; 'Destring Suck'
G1 E-1.25 F900
G1 X-2.05 Y-3.71 E0 F2612.3
G1 X-1.98 Y-3.93 E0
G1 X-1.8 Y-4.15 E0
G1 X-1.72 Y-4.37 E0
G1 X-1.6 Y-4.51 E0
G1 X-1.53 Y-4.71 E0
;
; 'Crown Path', 0.7 [feed mm/s], 27.2 [head mm/s]
G1 X1.53 Y-4.71 E0 F30000
; 'Destring Prime'
G1 E1.25 F900
G1 X1.6 Y-4.51 E0.0057 F1631.7
G1 X1.72 Y-4.37 E0.0048
G1 X1.8 Y-4.15 E0.0062
G1 X1.98 Y-3.93 E0.0078
G1 X2.05 Y-3.71 E0.0059
G1 X2.24 Y-3.48 E0.008
;
; 'Destring/Wipe/Jump Path', 0.0 [feed mm/s], 43.5 [head mm/s]
; 'Destring Suck'
G1 E-1.25 F900
G1 X2.05 Y-3.71 E0 F2612.3
G1 X1.98 Y-3.93 E0
G1 X1.8 Y-4.15 E0
G1 X1.72 Y-4.37 E0
G1 X1.6 Y-4.51 E0
G1 X1.53 Y-4.71 E0
;
; Post-layer lift
G1 X1.53 Y-4.71 Z4.5 E0 F210
; END_LAYER_OBJECT z=4.00
; BEGIN_LAYER_OBJECT z=4.250
; *** Slowing to match Min Layer Time (speed multiplier is 0.711907) ***
;
; 'Perimeter Path', 0.1 [feed mm/s], 7.1 [head mm/s]
G1 X2.05 Y6.84 Z4.5 E0 F30000
G1 X2.05 Y6.84 Z4.25 E0 F210
; 'Destring Prime'
G1 E1.25 F900
G1 X2.09 Y7.23 E0.007 F427.1
G1 X-2.29 Y7.23 E0.0775
G1 X-2.35 Y7.06 E0.0032
G1 X-2.35 Y3.19 E0.0684
G1 X-2.26 Y3.05 E0.003
G1 X-0.18 Y4.25 E0.0424
G1 X0.12 Y4.29 E0.0053
G1 X2.22 Y3.07 E0.043
G1 X2.35 Y3.13 E0.0025
G1 X2.35 Y7.05 E0.0693
G1 X1.92 Y6.96 E0.0078
;
; 'Loop Path', 0.3 [feed mm/s], 18.7 [head mm/s]
G1 X1.58 Y6.73 E0 F30000
G1 X-1.78 Y6.73 E0.0594 F1121.3
G1 X-1.84 Y6.64 E0.0019
G1 X-1.84 Y3.99 E0.0469
G1 X-1.71 Y3.95 E0.0025
G1 X-0.4 Y4.7 E0.0267
G1 X0.04 Y4.79 E0.008
G1 X0.4 Y4.71 E0.0065
G1 X1.71 Y3.95 E0.0268
G1 X1.84 Y3.99 E0.0025
G1 X1.84 Y6.55 E0.0453
;
; 'Loop Path', 0.5 [feed mm/s], 30.3 [head mm/s]
G1 X1.08 Y6.23 E0 F30000
G1 X-1.28 Y6.23 E0.0417 F1815.4
G1 X-1.34 Y6.12 E0.0022
G1 X-1.34 Y4.84 E0.0225
G1 X-1.25 Y4.79 E0.0018
G1 X-0.56 Y5.19 E0.0142
G1 X0.01 Y5.29 E0.0103
G1 X0.53 Y5.2 E0.0094
G1 X1.25 Y4.79 E0.0146
G1 X1.34 Y4.84 E0.0019
G1 X1.34 Y6.05 E0.0213
;
; 'Destring/Wipe/Jump Path', 0.0 [feed mm/s], 37.4 [head mm/s]
; 'Destring Suck'
G1 E-1.25 F900
G1 X1.08 Y6.23 E0 F2242.5
G1 X-1.28 Y6.23 E0
G1 X-1.34 Y6.12 E0
G1 X-1.34 Y4.84 E0
G1 X-1.25 Y4.79 E0
G1 X-0.56 Y5.19 E0
G1 X0.01 Y5.29 E0
G1 X0.53 Y5.2 E0
G1 X1.25 Y4.79 E0
G1 X1.34 Y4.84 E0
G1 X1.34 Y6.05 E0
;
; 'Perimeter Path', 0.1 [feed mm/s], 7.1 [head mm/s]
G1 X2.14 Y-3.55 E0 F30000
; 'Destring Prime'
G1 E1.25 F900
G1 X2.19 Y-3.09 E0.0082 F427.1
G1 X0.18 Y-4.25 E0.0411
G1 X-0.12 Y-4.29 E0.0053
G1 X-2.22 Y-3.07 E0.0429
G1 X-2.35 Y-3.13 E0.0025
G1 X-2.35 Y-7.06 E0.0695
G1 X-2.29 Y-7.23 E0.0033
G1 X2.29 Y-7.23 E0.081
G1 X2.35 Y-7.06 E0.0032
G1 X2.35 Y-3.33 E0.0659
G1 X2.03 Y-3.49 E0.0062
;
; 'Loop Path', 0.3 [feed mm/s], 18.7 [head mm/s]
G1 X1.66 Y-3.97 E0 F30000
G1 X0.4 Y-4.7 E0.0257 F1121.3
G1 X-0.04 Y-4.79 E0.0081
G1 X-0.4 Y-4.71 E0.0064
G1 X-1.71 Y-3.95 E0.0268
G1 X-1.84 Y-3.99 E0.0025
G1 X-1.84 Y-6.64 E0.0469
G1 X-1.78 Y-6.73 E0.0019
G1 X1.78 Y-6.73 E0.0629
G1 X1.84 Y-6.64 E0.0019
G1 X1.84 Y-4.19 E0.0435
;
; 'Loop Path', 0.5 [feed mm/s], 30.3 [head mm/s]
G1 X1.08 Y-4.89 E0 F30000
G1 X0.47 Y-5.22 E0.0123 F1815.4
G1 X-0.14 Y-5.29 E0.0108
G1 X-0.56 Y-5.19 E0.0077
G1 X-1.25 Y-4.79 E0.0142
G1 X-1.34 Y-4.84 E0.0018
G1 X-1.34 Y-6.12 E0.0225
G1 X-1.28 Y-6.23 E0.0023
G1 X1.28 Y-6.23 E0.0451
G1 X1.34 Y-6.12 E0.0023
G1 X1.34 Y-4.94 E0.0208
;
; 'Solid Path', 0.5 [feed mm/s], 26.7 [head mm/s]
G1 X-0.66 Y-5.61 E0 F30000
G1 X-0.86 Y-5.81 E0.005 F1601.8
G1 X0.55 Y-5.81 E0.0249
G1 X0.86 Y-5.51 E0.0077
;
; 'Destring/Wipe/Jump Path', 0.0 [feed mm/s], 37.4 [head mm/s]
; 'Destring Suck'
G1 E-1.25 F900
G1 X0.55 Y-5.81 E0 F2242.5
G1 X-0.86 Y-5.81 E0
G1 X-0.66 Y-5.61 E0
;
; Post-layer lift
G1 X-0.66 Y-5.61 Z5 E0 F210
; END_LAYER_OBJECT z=4.25
;
; *** Cooling Extruder 1 to 0 C and Retiring ***
; Guaranteed same extruder, but about to deselect, maybe retract before cooling down
; BfB-style
M104 S0
; 5D-style
M104 S0
;
; fan off
M107
; *** G-code Postfix ***
;
; All used extruders are already 'Cooled' to 0
;
;
;
; Estimated Build Time:   3.93 minutes
; Estimated Build Cost:   $0.00
;
; Filament used per extruder:
;    Ext 1 =    53.49 mm  (0.378 cm^3)
;
; *** Extrusion Time Breakdown ***
; * estimated time in [s]
; * before possibly slowing down for 'cool'
; * not including Z-travel
;	......................................................................................
;	: Extruder #1 : Extruder #2 : Extruder #3 : Extruder #4 : Path Type                  :
;	:.............:.............:.............:.............:............................:
;	: 12.0516     : 0           : 0           : 0           : Jump Path                  :
;	: 0           : 0           : 0           : 0           : Pillar Path                :
;	: 0           : 0           : 0           : 0           : Raft Path                  :
;	: 0           > 0           > 0           > 0           > Support Interface Path     :
;	: 0           : 0           : 0           : 0           : Support (may Stack) Path   :
;	: 96.1523     : 0           : 0           : 0           : Perimeter Path             :
;	: 59.8025     : 0           : 0           : 0           : Loop Path                  :
;	: 18.3843     > 0           > 0           > 0           > Solid Path                 :
;	: 2.67242     : 0           : 0           : 0           : Sparse Infill Path         :
;	: 4.43893     : 0           : 0           : 0           : Stacked Sparse Infill Path :
;	: 26.8203     : 0           : 0           : 0           : Destring/Wipe/Jump Path    :
;	: 5.1938      > 0           > 0           > 0           > Crown Path                 :
;	: 0           : 0           : 0           : 0           : Prime Pillar Path          :
;	: 0           : 0           : 0           : 0           : Pause Point                :
;	: 0           : 0           : 0           : 0           : Extruder Warm-Up           :
;	:.............:.............:.............:.............:............................:
; Total estimated (pre-cool) minutes: 3.76
