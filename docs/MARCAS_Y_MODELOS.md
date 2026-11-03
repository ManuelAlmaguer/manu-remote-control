# Marcas y modelos implementados en Manu Remote Control

Versión **1.0.2** (versionCode 3). Todas las marcas listadas funcionan sin conexión: viajan dentro del APK en `app/src/main/assets/catalog/manu_catalog.tsv.gz`.

| Resumen | Cantidad |
|---|---|
| Marcas distintas | 952 |
| Mandos en el catálogo (marca + modelo) | 1890 |
| Códigos IR individuales | 43.125 |
| Combinaciones marca / tipo de equipo | 1155 |

## Las 4 marcas solicitadas

Estas marcas **no publican códigos IR** y no aparecen en ninguna base de datos libre consultada (Flipper-IRDB/irdb.tk, LIRC, SmartIR, RemoteCentral). Para usarlas desde la app se incluyen **perfiles candidato**: cada perfil copia el mando real de un equipo chino de la misma familia de placa. Se prueban en orden (A, B, C, D) hasta que tu equipo responda.

| Marca | Tipo | Perfiles | Base de cada perfil |
|---|---|---|---|
| Haitech | Aires acondicionados | 2 | Chigo CS-21H3A-B155AF, Midea AC MAW05R1WBL |
| Haitech | Televisores | 4 | AOC Genérico, Haier L42C1180, Sanyo DP26640, TCL Roku TV |
| ATEC | Televisores | 4 | Sanyo DP26640, TCL Roku TV, AOC Genérico, Haier L42C1180 |
| Panda | DVD / Blu-ray | 1 | dato real de IRDB |
| Panda | Televisores | 4 | Sanyo ds32224, TCL Roku TV, AOC Genérico, Haier L42C1180 |
| HELECT | Televisores | 4 | Sanyo Genérico, TCL Roku TV, AOC Genérico, Haier L42C1180 |

> El **KT1440** (sección «Mandos universales» de la app) trae además la tabla oficial de códigos de este mercado: Haier 001/002, Panda 003, Atec 006/009, Konka 004/005, Soyea 010, Parker 007/008, cajas 101-109 y DVD 201-202.

## Mandos universales (sección nueva)

- **KT1440** (GSSC / WINFLIKE / ZdalaMit (genérico chino)) — 4 bloques de manual y 21 códigos de programación.

## Televisores — 183 marcas

| Marca | Modelos | Botones |
|---|---|---|
| ADS | Genérico | 43 |
| Akai | ATE 22Y604W | 24 |
| Amazon | FireTV Omni Series 4K | 32 |
| AmazonBasics | TV Remote | 10 |
| Android | TV MXQ | 18 |
| ANITECH | Genérico | 8 |
| AOC | Genérico | 25 |
| Apex | LE4643T | 12 |
| Apple | Genérico | 25 |
| Asus | Genérico | 31 |
| ATEC | Candidato A (base Sanyo DP26640); Candidato B (base TCL Roku TV); Candidato C (base AOC Genérico); Candidato D (base Haier L42C1180) | 129 |
| Avermedia | Genérico | 30 |
| AWA | MSDV3268O5D0 | 35 |
| Baird | T15011DLEDDS RC-6 | 49 |
| Bauhn | ATV 50FHD4; tv | 64 |
| BBK | TV LEM-1071 | 13 |
| Beko | Genérico | 33 |
| BGH | BLE2814D | 9 |
| Blaupunkt | Blaupunkt | 20 |
| Blitzwolf | BWPCM2 | 11 |
| Bolva | TV | 48 |
| Bose | TV | 4 |
| Brandt | B3228HD; B3230HD TV | 20 |
| Bush | 43QT24SB; TV DLED32287HDCNTDFVP; TV VL32HDLED | 111 |
| Cadillac | Escalade TV | 5 |
| CCE | RC512 Remote | 37 |
| Cce | Genérico | 29 |
| CIS BOX | Genérico; Genérico; Genérico; Genérico; Genérico; Genérico; Genérico | 64 |
| Coby | Genérico | 36 |
| Commodore | Genérico | 27 |
| ContinentalEdison | CELD55SQLDV24B6; CELED32JBL7; CELED502723 | 100 |
| Cranker | generic | 6 |
| Crown | 22111; RC647340 | 52 |
| Curtis Electronics | Genérico | 38 |
| Daewoo | d Parsed; Genérico; Genérico | 17 |
| Dell | Genérico | 35 |
| Denver | LED-3271 | 51 |
| Devant | Unknown Model | 13 |
| Digihome | Butlins | 12 |
| Digital Stream | Genérico | 34 |
| DigitalView | Genérico | 32 |
| DirecTV | Genérico; Genérico; Genérico; Genérico; Genérico; Genérico; Genérico; Genérico; Genérico; Genérico; Genérico; Genérico | 371 |
| Dual | DL-32HD-002 | 24 |
| Durabrand | Genérico | 30 |
| Dwin | Genérico | 24 |
| Dynex | DX-RC01A-12; EN21669D; RC-701-0A | 117 |
| DYON | Movie Smart 32 XT | 27 |
| EdenWood | TV | 26 |
| EHP | Waagen Universal Remote | 12 |
| Element | 100-Series TV; FullRemote; TV | 136 |
| Elements | TV | 13 |
| Elitelux | L32HD1000 | 38 |
| elWron | elWron | 46 |
| Emerson | 32FNT004; EWC13D4; remote NH303UD; Genérico; Genérico; Genérico | 198 |
| Enseo | Enseo; full | 47 |
| Erisson | AL52D B | 28 |
| Fast | Genérico; Genérico | 86 |
| Fetch | TV Box AUS | 41 |
| FINLUX | FINLUX | 39 |
| Fisher | Genérico; Genérico | 19 |
| Fujitsu | Genérico | 13 |
| Funai | Funai | 44 |
| Furrion | Furrion | 42 |
| GE | nérico | 32 |
| General Electric | Genérico | 27 |
| Gigabyte | AORUS Monitor; Genérico | 31 |
| Gogen | TVH24P266T | 20 |
| GPX | Gpx | 11 |
| Grandin | Grandin; Unknown Model | 38 |
| Grundig | AndroidTV; TP750C; TP800; Genérico | 138 |
| GuestTek | Marriot Hotel | 44 |
| GVA | GVA32M7D | 20 |
| Haier | L42C1180 | 48 |
| Haitech | Candidato A (base AOC Genérico); Candidato B (base Haier L42C1180); Candidato C (base Sanyo DP26640); Candidato D (base TCL Roku TV) | 129 |
| Hauppauge | R-005; Genérico; Genérico; WinTV DualHD | 145 |
| HELECT | Candidato A (base Sanyo Genérico); Candidato B (base TCL Roku TV); Candidato C (base AOC Genérico); Candidato D (base Haier L42C1180) | 117 |
| Hewlett Packard | Genérico; Genérico | 69 |
| Hisense | 32A4HAU; EN3AH39H | 99 |
| Hitachi | 49141; CLE-1031; LE46H508; Genérico; Genérico; Genérico; Genérico; Genérico; Genérico | 293 |
| Hotels | Hilton Connected Room Remote; Marriott-BONVoY Remote | 49 |
| Humax | Genérico; Genérico; Genérico; Genérico | 156 |
| Insignia | Insignia; NS 40D510NA17; Genérico | 87 |
| JVC | 4KTV; Genérico; LT-49HW97U; RMT-JR01; Genérico; Genérico; Genérico; Genérico | 198 |
| KAWA | Genérico | 33 |
| Kendo | CP20M36VT | 12 |
| Kogan | KALED50XU9210STB | 33 |
| Kraft | KTV | 15 |
| Kworld | Genérico | 44 |
| LG | 32LF650V; 32LW4500; Genérico; MKJ39170828 Service; Genérico; Genérico | 273 |
| LodgeNet | lrc3220 | 15 |
| Loewe | TV; Genérico; Genérico; Genérico; Genérico; Genérico | 139 |
| LXI | Genérico | 30 |
| MAGNASONIC | Genérico | 28 |
| Magnavox | 20MT1331 17; CD130MW8; RD0946T102; Genérico | 111 |
| Manta | Manta; TV 2 | 29 |
| Marantz | Genérico; Genérico; Genérico; Genérico; Genérico; Genérico | 63 |
| Matsui | 1435b | 23 |
| Mediasonic | Homeworx HW180STB | 9 |
| Medion | MD21302; MD31802; Medion | 81 |
| Memorex | Genérico | 22 |
| Mitsubishi | Genérico; Genérico; Genérico; Genérico; Genérico; Genérico; Genérico | 214 |
| Mivar | LCD TV | 14 |
| Motorola | Genérico | 29 |
| Nagra | Genérico | 35 |
| NEC | E425; NEC; Genérico; Genérico; Genérico | 111 |
| Neo | TV | 14 |
| NET TV | Genérico | 29 |
| Nevir | NVR-7409-39HD-N | 50 |
| Now Broadband TV | Genérico | 40 |
| Oky | TV | 7 |
| One For All | OFA 8 Universal Remote | 16 |
| Onida | Genérico | 28 |
| Onn | Roku TV | 28 |
| orion | Genérico | 38 |
| Panasonic | 58JX800 Series; Genérico; N2QAYA 152; N2QAYB000752 Full; Genérico; Genérico; Genérico; Genérico | 314 |
| Panda | Candidato A (base Sanyo ds32224); Candidato B (base TCL Roku TV); Candidato C (base AOC Genérico); Candidato D (base Haier L42C1180) | 121 |
| PDi | PD108-420 | 43 |
| Philips | 14GX8510; 32PFL7962D12; Genérico; Genérico; TV 14PV172 08; Genérico | 140 |
| Pinnacle Systems | Genérico | 24 |
| Pioneer | Kuro PDP LX508A; Genérico; Genérico | 119 |
| PixelView | Genérico | 36 |
| Platinum | PT2020LED | 41 |
| PrismPlus | PrismPlus | 14 |
| Proscan | Genérico | 50 |
| Proton | Genérico; Genérico; Genérico; Genérico | 59 |
| Quasar | Genérico | 20 |
| RCA | RCR314WR  RCA313BR; RokuTV RTR4061-B-CA; RT2471 AC; Genérico; Genérico; Genérico | 134 |
| remotec | Genérico | 25 |
| RemotesReplaced | RRS41 | 38 |
| ReplayTV | Genérico | 33 |
| Roku | Standalone | 11 |
| Samsung | AA59-00484A; BN59-01179A; LE40R87BD; Genérico; Genérico; Genérico | 223 |
| Sankey | TV | 55 |
| Sanyo | DP26640; ds32224; Genérico; universal; Genérico; Genérico | 137 |
| Sceptre | 8142026670003C; H24 | 86 |
| SEG | MALTA | 22 |
| Seiki | SE40FYP1T TV | 41 |
| Seleco | Genérico; Genérico | 17 |
| Sencor | 25801 | 12 |
| Sharp | Aquos JP; Genérico; g0684cesa NES TV; LC-RC1-16; Genérico | 149 |
| Silver | LE410004 | 15 |
| SkyVue | 03SI-RI; SkyVue | 44 |
| Soniq | E32W13B; QSP500TV6; Z24Z15B | 79 |
| Sony | Bravia KD-55XF80xx-49XF80xx-43XF80xx; RMT TX100D; SFRTV5; Genérico; Genérico; Genérico; Genérico; Genérico; Genérico; Genérico; Genérico; Genérico | 468 |
| Strong | 32HX4003; RCU-Z400N; TVD221 B1825 | 106 |
| Sunbrite | Sunbrite | 13 |
| Supra | STV LC32T880WL 1 | 26 |
| Sweex | Generic Monitor | 3 |
| Symphonic | ST424FF; Genérico | 44 |
| Syntax-Brillian | SyntaxBrillian Olevia232T | 14 |
| TCL | 50S423; Roku TV | 44 |
| TCUAG | Genérico | 41 |
| Technika | 22880 | 39 |
| TechniSat | DVR401B | 14 |
| Telecontrole | TC 33 VNS2000 | 22 |
| Telefunken | d32f660x5cwi; D40F294R4CW; L55U405B4CW | 134 |
| TELEFUNKEN | Genérico; Genérico | 42 |
| Telekom | Entertain | 40 |
| Telemann | Genérico | 7 |
| Tevion | 3221TS | 16 |
| Thomson | Genérico | 26 |
| Toshiba | CT-32F2; Ct-8563; SE R0305 32CV100U; Genérico; Genérico | 186 |
| Trutech | Genérico | 22 |
| Twinhan | Genérico | 31 |
| Typhoon | Genérico | 31 |
| United | tv | 13 |
| Universal | Genérico | 3 |
| User Created | UniversalSamsungFF | 38 |
| Vestel | Genérico | 39 |
| Viano | STV65UHD4K | 13 |
| ViewSonic | A-00010219; RC52A 11; VT2645 | 74 |
| Viore | TV LC37VF72 | 36 |
| Vitec | Exterity IPTV | 16 |
| Vizio | V405-H19; XRT122; XRT136 | 135 |
| Waltham | WTHD3214B | 12 |
| Watson | FA3627T | 8 |
| Wbox | TV | 14 |
| WD | Genérico | 17 |
| Westinghouse | RMT-13; Westinghouse | 52 |
| Xiaomi | TV | 11 |
| Yamaha | Genérico | 16 |
| Yuan | Genérico | 37 |
| Zenith | SC3492Z; tv; Genérico; Genérico; Genérico; Genérico | 105 |

## Aires acondicionados — 124 marcas

| Marca | Modelos | Botones |
|---|---|---|
| AccessHD | Genérico | 26 |
| AccessMedia | Genérico | 26 |
| Accupel | Genérico | 25 |
| Acer | Genérico | 26 |
| Acesonic | Genérico | 26 |
| Aconatic | Genérico | 26 |
| Acorp | Genérico | 26 |
| Admiral | AC | 9 |
| Advanced Acoustics | Genérico | 9 |
| Airfel | AFSW-12HAR1RCIO | 6 |
| Airmax | Airmax | 7 |
| Airmet | ac | 12 |
| Airwell | Airwell; AWSI-PNXA012-N11 | 22 |
| Aldi | Easy Home Portable Air Cooler | 7 |
| Amcor | AC | 7 |
| Arctic King | Arctic King RG15B1 | 9 |
| Argo | ac; zelos | 7 |
| Ariston | AC A-MW09-IGX | 2 |
| Audio Access | Genérico; Genérico; Genérico | 45 |
| AutumnWave | Genérico | 26 |
| Ballu | R05-BGE | 7 |
| Beko | Beko | 7 |
| Black and Decker | Portable | 9 |
| Bonaire | DurangoAC | 4 |
| Boston Bay | Boston Bay MSAB 09CR | 19 |
| Botti | BL-168DLR | 6 |
| Boulanger | AC | 9 |
| Carrier | orlen ac | 6 |
| Chief Manufacturing | Genérico; Genérico | 18 |
| Chigo | CS-21H3A-B155AF; KRF-51G 79F | 50 |
| Ciatronic | AC | 1 |
| Classe Audio | Genérico; Genérico | 32 |
| Comfort Aire | Comfort Aire RG57A6 | 16 |
| Corlitec | Cortlitec portable ac | 11 |
| COTech | MPPH-08CRN7-QB6 AC | 13 |
| Daikin | ARC480A41; ARC480A53 | 35 |
| Danby | DAC060EB7WDB | 4 |
| DeLonghi | Pinguino EL290HLWKC; Pinguino PAC EL92 SILENT | 17 |
| Eberg | RIO R29E1 | 10 |
| Ecofort | CoolAir 7+ EQCA7+2900824 | 11 |
| Electra | smart | 2 |
| electriQ | P15C-V2 | 6 |
| Electrolux | Electrol ESV09CRO B2I | 17 |
| Emerson | EARC8RE1 ac; Quiet Kool AC | 17 |
| Eurom | PAC 9.2; Tristar AC | 13 |
| Fedders | Genérico | 6 |
| Ferroli | Ferroli | 8 |
| Firstline | AAS2500 | 10 |
| Frico | PA2510E08 | 3 |
| Friedrich | 4235h; Genérico; Genérico; Friedrich | 33 |
| Frigidaire | AC; RG15D AC | 21 |
| Fujidenzo | FEA5001 AC | 5 |
| Fujitsu | AC; AC ASU18RLF | 33 |
| General Electric | GE AC; GE Window AC | 17 |
| GoodHome | FDP15 | 7 |
| GREE | KFR-70G-A1 | 8 |
| Haier | AC AS18GD3HRA; AC HPD10XCM-LW | 14 |
| Haitech | Candidato A (base Chigo CS-21H3A-B155AF); Candidato B (base Midea AC MAW05R1WBL) | 38 |
| Hewlett Packard | Genérico; Genérico | 40 |
| Hip Interactive | Genérico | 15 |
| Hisense | DG11J1-99 celsius | 26 |
| Hitachi | Genérico; Genérico; Genérico; Genérico; Genérico; Genérico; Genérico; Hitach RAK-18QH8; Genérico; Genérico; Genérico; Genérico; Genérico; Genérico | 158 |
| Hokkaido | Genérico | 26 |
| Honeywell | MN10CESS | 6 |
| Innova | 2.0 | 12 |
| Insigna | AC NSRC2AC9; Insignia NS-AC8PWH9-C AC | 22 |
| Interact | Genérico | 16 |
| Inventum | AC | 9 |
| Kelon | AS-24HR4SQJUL | 26 |
| Kenmore | AC | 11 |
| Klarstein | Metrobreeze Miami; Skyscraper | 14 |
| Koldfront | WAC12001 | 9 |
| Lacie | Genérico; Genérico | 42 |
| Lamborghini | Unknown Model 1 | 7 |
| Legion | LE-F30RH-IN | 26 |
| Lennox | AC | 14 |
| LG | COV30332906 AC; LP1015WNR AC | 23 |
| Lifetime Air | Lifetime Air | 8 |
| Logik | HLF-20R | 7 |
| Macro Image Technology | Genérico | 26 |
| MayTag | M6X06F2A | 6 |
| Meliconi | Genérico | 26 |
| Midea | AC MAW05R1WBL; silent cool 26 pro | 23 |
| Mitsubishi | MSH GA71VB; SRK35ZS-W | 21 |
| Moretti | Air Cooler | 6 |
| OK | AC OAC 7020 | 4 |
| Oreck | AIR12GU | 4 |
| Osaka | CH 09 DSBP | 6 |
| Pace | Genérico; Genérico | 37 |
| Packard Bell | Genérico | 26 |
| Panasonic | A75C4187; CWA75C4179 | 22 |
| Pinnacle Systems | Genérico; Genérico | 51 |
| Pioneer | MiniSplit | 14 |
| Princess | AC | 6 |
| Radio Shack | Genérico; Genérico | 28 |
| Remko | RKL | 7 |
| Rinnai | AC | 9 |
| Royal Clima | Royal Clima RC-TWN55HN | 7 |
| Samsung | AC AR12K; Genérico; AR13TYHZCWKN | 47 |
| SereneLife | SLPAC8 | 11 |
| Sharp | AH X9VEW AC; CVP10MX | 26 |
| Shivaki | SSA18002 | 24 |
| Sinclair | ASH13BIF2 | 9 |
| SoleusAir | Portable; SoleusAir | 24 |
| Subtropic | in-07HN1 | 8 |
| SuedwindAmbientika | Suedwind | 16 |
| Suntec | Transform AC | 6 |
| TCL | Tcl | 10 |
| TEAC | Genérico; Genérico; Genérico; Genérico | 65 |
| Timberk | RG05D4-BGE | 22 |
| Tora | TS 16-Classic | 26 |
| Toshiba | AC; RG57H4 | 22 |
| Tosot | T24H-ILF | 26 |
| Tropic | AC | 6 |
| Trotec | PAC2600X | 6 |
| Turtle Beach | Genérico; Genérico | 28 |
| Turtlebeach | Genérico | 26 |
| Vornado | Vornado | 3 |
| Whynter | AC | 8 |
| Windfree | Ac test; Actest2 | 11 |
| Xiaomi | AC | 7 |
| York | AC; full working | 40 |
| Zenith | AC | 7 |
| Zephir | ZPO9001H AC | 8 |

## DVD / Blu-ray — 113 marcas

| Marca | Modelos | Botones |
|---|---|---|
| ABIT | Genérico | 36 |
| Apex | Genérico; RM 3800 | 90 |
| Arcam | Genérico; Genérico | 94 |
| Audio Authority | Genérico; Genérico | 61 |
| Axonix | Genérico | 68 |
| Bluesky | Genérico | 32 |
| Bose | 3-2-1 Series 1 | 36 |
| Brandt | DVDP-7R | 23 |
| Cambridge Audio | Genérico | 46 |
| Cary Audio Design | Genérico | 35 |
| CAT | Genérico | 39 |
| Century Concept | Genérico | 16 |
| Cinemateq | Genérico | 39 |
| CIS BOX | Genérico; Genérico; Genérico | 72 |
| Classe Audio | Genérico; Genérico; Genérico; Genérico; Genérico | 178 |
| Coby | Genérico | 15 |
| Cyberhome | Genérico; Genérico; Genérico | 122 |
| Daewoo | Genérico | 42 |
| Dantax | Genérico | 41 |
| Denon | Genérico; Genérico; Genérico; Genérico; Genérico; Genérico; Genérico | 261 |
| Denver | Genérico | 44 |
| DIFRNCE | Genérico | 44 |
| DK Digital | Genérico | 33 |
| DVDO | Genérico; Genérico; Genérico | 130 |
| E Max | Genérico | 41 |
| Elan | Genérico | 45 |
| Emerson | Genérico | 38 |
| Escient | Genérico; Genérico; Genérico; Genérico; Genérico | 142 |
| Esoteric Audio | Genérico | 44 |
| Faroudja | Genérico; Genérico | 44 |
| Fisher | RVR-4000 | 31 |
| FSC | Genérico | 50 |
| Fusion Research | Genérico | 35 |
| GAMEFACTORY | Genérico | 17 |
| Go Video | Genérico | 36 |
| Goodmans | Genérico | 39 |
| GPX | D2816 | 46 |
| Harman Kardon | Genérico; Genérico; Genérico; Genérico | 200 |
| Hitachi | Genérico | 38 |
| Hiteker | Genérico | 35 |
| Humax | Genérico | 39 |
| Hyundai | Genérico | 47 |
| I-O Data | Genérico | 46 |
| Insignia | Genérico; Genérico; NSWBRDVD2 | 93 |
| Integra | Genérico; Genérico; Genérico; Genérico | 231 |
| IR4PS3 | Genérico | 52 |
| Jamo | Genérico | 54 |
| JVC | Genérico; Genérico; Genérico; Genérico; HR-J700E; HR-S2902U; XVBP1 | 268 |
| Kaleidescape | Genérico | 71 |
| Kenwood | Genérico; Genérico; Genérico | 129 |
| Krell | Genérico | 48 |
| Lexicon | Genérico | 29 |
| LG | AKB35840202 DVD; AKB73495301; Genérico; DKS-6100Q; Genérico; Genérico; Genérico; DVX7900; GC260W; LHB655 | 419 |
| LiteOn | Genérico | 35 |
| Loewe | Genérico; Genérico; Genérico; Genérico; Genérico | 124 |
| M3 Electronic | Genérico | 29 |
| Magnavox | Genérico; Genérico; Genérico; Genérico; N9377; NB098; NB179 MWD 2206 | 239 |
| Malata | Genérico | 40 |
| Manhattan | Genérico | 36 |
| Manta | Genérico; Genérico | 87 |
| Marantz | BD8002; Genérico; Genérico; Genérico; Genérico; Genérico; Genérico; UD7007 (RC006UD) | 294 |
| McIntosh | Genérico | 35 |
| Memorex | Genérico; MVR4040A | 44 |
| Meridian | Genérico; Genérico; Genérico | 99 |
| Micromega | Genérico | 30 |
| Microsoft | Genérico | 17 |
| Mintek | Genérico | 24 |
| Mitsubishi | Genérico; Genérico; Genérico; Genérico | 165 |
| Mustek | Genérico; Genérico | 95 |
| NAD | T557 | 18 |
| Nakamichi | Genérico | 36 |
| Onkyo | Genérico; Genérico | 42 |
| Onkyo Integra | Genérico | 50 |
| Oppo | BDP103; BDP93; DV-970HD | 123 |
| Panasonic | DMP BD75; DMRBWT720; Genérico; Genérico; Genérico; Genérico; Genérico; Genérico; Genérico; DVD TV; EUR7621070; Light Tower LSSQ0342; VCR PV9662 | 375 |
| Panda | Genérico | 44 |
| Philips | Genérico; Genérico; Genérico; Genérico; Genérico | 167 |
| Pioneer | BDP150; BDP43FD; CU-DV027; Genérico; Genérico; Genérico; Genérico; GGF1381; LaserActive CU-CLD085; VXX2914 | 296 |
| Polaroid | Genérico | 44 |
| Prinz | DVD Player T182 | 48 |
| Proscan | Genérico; Genérico | 39 |
| Provision | Genérico; Genérico | 79 |
| Raite | Genérico | 43 |
| RCA | Genérico | 44 |
| Re.x | Genérico | 50 |
| roadstar | Genérico | 38 |
| Rowa | Genérico | 43 |
| S2 Digital | dvd-p2 | 36 |
| Samsung | B59-01301A; BD-D5300; Genérico | 124 |
| Sanyo | Bluray NC088 | 27 |
| Scott | Genérico; Genérico | 86 |
| SEG | Genérico | 42 |
| Sharp | BDHP20 | 15 |
| Sigma Designs | Genérico | 36 |
| Soniq | B100 | 20 |
| Sony | BD RMT B110A; BDP-S370; Genérico; Genérico; Genérico; Genérico; Genérico; Genérico; DVD RMTD185A RMTD165A; SLV-SE610B; Genérico; Genérico | 436 |
| Strato | DVD507 | 48 |
| Sylvania | SDVD1111 | 18 |
| TEAC | Genérico; Genérico | 37 |
| Technosonic | Genérico | 41 |
| Tevion | Genérico | 34 |
| Theta Digital | Genérico; Genérico | 32 |
| Tokai | Genérico | 42 |
| Toshiba | Genérico; SDKV550; SE R0108; SE R0420; SE-R0398 | 190 |
| United | Genérico | 45 |
| Unknown | 1 | 2 |
| Unknown Brand | Dvd tv player; Dvd tv player 2 | 49 |
| Vistron | Genérico | 41 |
| Vivanco | Genérico | 31 |
| Vizio | VBR220; VBR333 | 45 |
| Yakumo | Genérico | 44 |
| Yamaha | BDS667; Genérico; Genérico; Genérico; Genérico; Genérico; Genérico; Genérico; Genérico; Genérico; Genérico; Genérico | 520 |
| Yamakawa | Genérico | 43 |

## Audio y soundbars — 137 marcas

| Marca | Modelos | Botones |
|---|---|---|
| Adastra | WA215 | 14 |
| Adcom | Genérico; Genérico; Genérico | 84 |
| AEG | AEG | 18 |
| Aiwa | Genérico; Genérico; Genérico; Genérico | 27 |
| Amazon | Firetv EVG487 | 12 |
| Arcam | Genérico; Genérico; Genérico; Genérico; Genérico; Genérico; Genérico; Genérico | 220 |
| Architectural Audio | Genérico | 31 |
| ASR | EMITTER1HD | 7 |
| Audac | IMEO2 | 16 |
| Audio Control | Genérico; Genérico; Genérico | 107 |
| Audioengine | a5 | 4 |
| Auna | AV2 CD508 | 31 |
| Bauhn | MD44055 | 17 |
| Bestisan | 7020 | 20 |
| Biseoamz | DY29S | 21 |
| BnK Components | Genérico; Genérico; Genérico; Genérico; Genérico; Genérico; Genérico; Genérico; Genérico; Genérico; Genérico | 684 |
| Bomaker | Odine Soundbar. | 7 |
| Bose | Genérico; Genérico; Solo 5; Soundbar 300; SoundTouch; Wave Soundtouch IV | 154 |
| BrandUnknown | Amz snd bar; BLAUPUNKT SB1021 | 16 |
| Bumpboxx | Bumpboxx | 30 |
| Cambridge Audio | Genérico; Genérico; Genérico; Genérico | 127 |
| CambridgeAudio | 650A | 28 |
| Canton | Smart10 | 17 |
| Carver | Genérico; Genérico; Genérico; Genérico | 47 |
| Cary Audio Design | Genérico | 39 |
| CD Players | Kenwood RC-406; Pioneer CXC2665 | 42 |
| CIS BOX | Genérico; Genérico; Genérico; Genérico; Genérico; Genérico | 75 |
| Comhear | YARRA 3DX | 18 |
| Como Audio | Como audio | 12 |
| Craig | CHT904; CHT912; CHT921 | 45 |
| Creative | Soundbar creative stage; SoundBlaster Katana MF8245; z5400 | 53 |
| Crestron | Genérico | 64 |
| Delphi | Genérico | 48 |
| Denon | AVR-2113CI Main; Genérico; Genérico; Genérico; DHT S216; Home 550; RC 1199; RC1253; Genérico; Genérico; Genérico; Genérico; Genérico; Genérico; Genérico; Genérico; Genérico; Genérico; Genérico; Genérico; Genérico; Genérico | 697 |
| DirecTV | Genérico; Genérico; Genérico; Genérico; Genérico; Genérico; Genérico; Genérico | 297 |
| DollarTec BT5AmpBoard | DollarTec BT5AmpBoard | 7 |
| Douk Audio | Douk ST-01 Pro | 21 |
| Dual | SWR9 | 5 |
| DutchOriginals | dutch originals soundbar | 7 |
| DVD Players | Left Display | 40 |
| Eastern | DA 9000 | 13 |
| Edifier | r1700bt; r2000db; RC20G; S760D | 56 |
| Elac | EA101EQ-G | 10 |
| Emotiva | PT100 TA100 | 8 |
| Escient | Genérico; Genérico | 44 |
| Fisher | SLIM-1500 | 14 |
| Fluance | AI40 | 12 |
| Fosi Audio | Fosi Audio ZD3 | 13 |
| Fossi Audio | ZH3 | 13 |
| Generic Unknown Brand | Dream Aurora AC6923; Quacker LED Speaker | 50 |
| Geneva | Sound System Model L | 22 |
| GPX | cd radio; hc221b | 11 |
| Grundig | CMS 5000; DSB 950 | 56 |
| Harman Kardon | Harman Kardon AVR130; Harman Kardon bds580; Genérico; Genérico; Genérico; Genérico; Genérico; Genérico; Genérico; Genérico; Genérico | 280 |
| Hisense | HS215 | 13 |
| Hitachi | HSB40B16 | 5 |
| Homecast | Genérico | 47 |
| IBIZA Sound | IBIZA PORT 15 UHF | 24 |
| iLive | ITP280B Remote; Soundbar | 55 |
| Integra | DTR-7; Genérico; Genérico; Genérico; Genérico; Genérico; Genérico; Genérico; Genérico; Genérico; Genérico; Genérico; Genérico; Genérico | 488 |
| JBL | CINEMA SB120; CINEMA SB150; LSR4326P; On Stage IIIP Speaker; Genérico | 87 |
| JVC | CA-D851TR; D62BM; Genérico; Genérico; Genérico; Genérico; Genérico; Genérico; Genérico; Genérico; Genérico; Genérico; Genérico; RM-RK258; THBY370A | 403 |
| Kanto YU | 5 | 22 |
| Kathrein | Genérico | 72 |
| KEF | LSX Speakers | 8 |
| Kenwood | Genérico; Genérico; Genérico; Genérico; Genérico; Genérico; Genérico; Genérico; Genérico; Genérico; Genérico; Genérico; Genérico; Genérico | 465 |
| Kinergetics Research | Genérico | 26 |
| Klipsch | fives; r15pm; Soundbar | 39 |
| Kogan | KASBD150HA | 14 |
| Krell | Genérico | 1 |
| Larksound | L200 | 20 |
| Lexicon | Genérico | 39 |
| LG | AKB74815321; LG; NB5541 | 65 |
| Logitech | Logi WD216XM; Z-5500; Z906 | 36 |
| Luxman | Genérico | 21 |
| Madrigal | Genérico | 43 |
| Majority | K2 SoundBar | 12 |
| Marantz | RC042SR; RC2100DR CD; RC2100DR CDR; Genérico; Genérico; Genérico; Genérico; Genérico; Genérico; Genérico; Genérico; Genérico; Genérico; SR 7009 (RC026SR) | 317 |
| McIntosh | Genérico | 53 |
| MCM Custom Audio | MCM Volume Control 50-8394 | 3 |
| Microlab | RC071; SOLO11 | 22 |
| Mitsubishi | Genérico; Genérico; Genérico; Genérico | 229 |
| Myryad | Genérico; Genérico; Genérico; Genérico | 33 |
| NAD | 712; Amp 1; Genérico | 89 |
| Naim | Muso | 7 |
| Nakamichi | Genérico; Genérico; Genérico; Genérico; Genérico; RM 4TA | 302 |
| Nav Unit | TOYOTA AVALON XLS 2003-2004 | 29 |
| NEC | Genérico; Genérico; Genérico; Genérico; Genérico; Genérico | 63 |
| Onkyo | DX-7333; RC866M Receiver; RC898M; Genérico; Genérico | 251 |
| Onkyo Integra | Genérico; Genérico | 31 |
| Onn | sound bar and subwoofer | 13 |
| Ottolives | ROHS BL XK01 | 17 |
| Panasonic | N2QAYB000145; N2QAYB000210; Genérico; Genérico; Genérico; RXDS750; SC-HC58; SCH-TB8 | 128 |
| Parasound | Genérico; Genérico; Genérico | 97 |
| Pheanoo | P27 | 13 |
| Philips | CD Player 723; FW-M55; mcm2000; Genérico; Genérico; Genérico; Genérico | 121 |
| Pioneer | CU P0045; QXE1044; Genérico; Genérico; XXD3089; XXD3105 | 201 |
| PolkAudio | POLK RE9641 1 | 18 |
| Portta | Toslink Audio Switcher | 3 |
| PowerAcoustik | CP71W | 21 |
| Promethean | ActivSoundBar ASB-40 | 6 |
| Pyle | P2203ABTU | 48 |
| Q Acoustics | Q Acoustics M40 | 7 |
| Quantis | LSW-1 | 8 |
| RCA | RTS7110B2 | 9 |
| Rega | IO; Genérico; Genérico; Genérico | 48 |
| Revo | Superconnect BZAWDFB0315H2 | 28 |
| Rio | Sonic Blue 059PXC | 29 |
| Roberts | MP23 | 32 |
| Rockola | juke | 27 |
| Ruark | MR1 | 6 |
| Sagem | Genérico | 33 |
| Samsung | AH59-02547B Soundbar; MX-T50 | 44 |
| Sangean | RC30 | 12 |
| Sennheiser | Ambeo Soundbar | 8 |
| Sherwood | Genérico | 13 |
| Sky | SoundBox Devialet | 6 |
| SMSL | RC-8A | 11 |
| Sonos | ARC Beam Playbar Playbase | 4 |
| Sony | Amp STR-DE875; CMT-SBT20B; MHC-GS300AV; minidisk-deck; Genérico; Genérico; Genérico; Genérico; Genérico; Genérico; Genérico; Genérico; Genérico; Genérico; Genérico; Genérico; Genérico; Genérico; Genérico; RM-AAU130 AV; RMT-AA400U; Soundbar HT-XT3; SRS-GU10iP | 723 |
| Steljes | Desktop Speakers | 4 |
| TaoTronics | TT-SK023 Sound Bar; TT-SK026 | 25 |
| TCL | TS5010; TS8132 | 52 |
| TEAC | RC-505 Remote Control Unit | 30 |
| Technics | EUR646496; ST-HD60 | 80 |
| Teufel | 3SIXTY; CEM500RC; Cinebase Soundbar | 35 |
| Toshiba | RM-V329; SBX4250 | 33 |
| Ultimea | Poseidon M20 | 19 |
| Unknown | Unkown KC-806 | 29 |
| V-TAC | Soundor8 VT-6000W | 21 |
| Vizio | V51-H6; V51x-J6 | 113 |
| Winnes | KC809 | 29 |
| X4TECH | X4 TECH TU-1200 | 2 |
| Xiaomi | Binnifa Live1T; MITVS26 | 28 |
| Yamaha | CDC600; Genérico; Genérico; Genérico; Genérico; Genérico; Genérico; RX; RX-V795RDS; SR-B20A; Genérico; Genérico; Genérico; ZP45780 | 446 |
| Yarra | 3DX | 18 |
| Zinwell | Genérico | 26 |

## Proyectores — 74 marcas

| Marca | Modelos | Botones |
|---|---|---|
| 3M | Genérico | 16 |
| ABOX | T22 | 12 |
| Acer | H6517ST; Genérico; XL2530 IR29033 | 32 |
| Anker | Nebula Capsule Mini Projector; NebulaMarsLite | 24 |
| Apeman | LC650 | 17 |
| Artlii | YG300 | 12 |
| Barco | Genérico; Genérico | 80 |
| BenQ | Genérico; Genérico; Genérico; TK800M; W1070 | 109 |
| Boxlight | Genérico; Genérico; Genérico | 21 |
| BrandUnknown | Generic Projector; Generic Universal Remote | 34 |
| Byintek | P10 | 16 |
| Canon | LV-X320; Genérico | 42 |
| Casio | YT-130; YT-150 | 40 |
| CIS BOX | Genérico | 15 |
| Coolux | X3S | 13 |
| Da-Lite | ProjectorScreen | 3 |
| DBPower | Genérico | 12 |
| Dell | projector; tsfm ir01; Genérico | 50 |
| Denon | Projector | 3 |
| Digital Projection | Genérico; Genérico | 109 |
| Dragonfly | Screen | 5 |
| Dream Vision | Genérico | 38 |
| Dwin | Genérico; Genérico; Genérico | 80 |
| Eiki | CMXA Remote; Projector | 50 |
| EliteScreens | 100H | 3 |
| Elmo | Genérico | 12 |
| Epson | EBX7 154720000; Genérico; projector remote 159917600; Genérico | 151 |
| Faroudja | Genérico; Genérico; Genérico; Genérico | 55 |
| Gateway | 210 projextor | 20 |
| Groview | Groview | 13 |
| Harman Kardon | Genérico; Genérico; Genérico | 45 |
| Harman Video | Genérico; Genérico | 24 |
| Hitachi | CP-X2011 Projector; Genérico; Genérico | 108 |
| InFocus | Genérico; Many; Navigator 3; Genérico; Genérico; Genérico; Genérico; Genérico; Genérico; Genérico; Genérico | 290 |
| Jinhoo | M10 | 13 |
| JVC | LX-UH1B; Genérico; Genérico; Genérico | 174 |
| Knoll | Genérico | 20 |
| LG | PH300-NA | 33 |
| Marantz | Genérico; Genérico; Genérico | 75 |
| Maxell | MC-EU5001 | 31 |
| Minolta | MN674 | 24 |
| Mitsubishi | Genérico | 33 |
| NEC | RD469e; RU M124; Genérico; Genérico | 252 |
| NexiGo | PJ20 | 13 |
| ONOAYO | Portable Projector | 15 |
| Optoma | DH1011; Remote HOF04K276D6 | 57 |
| Panasonic | Projector; PT-AR100U | 22 |
| Philips | NeoPix Prime Projector | 12 |
| Promethean | PRM 35; UST-P1 | 54 |
| PVO | YG300Pro | 13 |
| Rayfoto | Projector | 16 |
| RIF6 | cube-projector-raw | 11 |
| Runco | Genérico; Genérico; Genérico | 85 |
| SAKAWA | Projector | 3 |
| Samsung | Freestyle Gen2; VG-TM2360E | 39 |
| Sanyo | Genérico | 24 |
| Sharp | RRMCGA664WJSA Notevision XR-32S-L; Genérico | 39 |
| SMART | Projectors | 13 |
| Sony | RM PJ1000; RM PJ24; Genérico; Genérico | 272 |
| StereoBoomm | MMP-250 | 15 |
| Thunrlge | X1BQ | 15 |
| TOPTRO | TR25 | 14 |
| TopVisionTec | TopVision | 12 |
| Vamvo | YG300 Pro Mini | 13 |
| Vankyo | V630W | 16 |
| Vidikron | Genérico | 27 |
| ViewSonic | Projector PA503W-2; X1 Projector | 44 |
| Vivitek | 2 Projector; Vivitek | 36 |
| Wanbo | T6 Max | 14 |
| WeWatch | v10 Projector | 13 |
| WiMiUS | K1; s25 | 27 |
| Yamaha | Genérico; Genérico; Genérico; Genérico | 212 |
| Yoton | Y3 | 15 |
| Zenith | Genérico; Genérico | 33 |

## Ventiladores — 93 marcas

| Marca | Modelos | Botones |
|---|---|---|
| Acoolir | AC-018 | 8 |
| Airmate | Genérico | 7 |
| AlohaBreeze | 84001 | 5 |
| Amazon | Basics Fan | 5 |
| Ansio | Fan 30inch Tower | 5 |
| Anslut | Tower Fan 95cm | 5 |
| Atomberg | fan | 10 |
| Bell+Howell | Socket Fan Light 8563 | 4 |
| Better Homes and Gardens | Better Homes and Gardens Fan | 5 |
| Big Ass Fans | Big Ass Fans Haiku | 10 |
| Bionaire | BASF1016GRC; Bionaire | 9 |
| Black and Decker | Black and Decker BFSR16W | 3 |
| Brandson | fan 303352; Fan 304997 | 13 |
| Cascade | Tower Fan | 5 |
| Cecotec | 1010 EXTREMECONNECT | 6 |
| Co Tech | CO TECH towerfan | 5 |
| ComfortZone | CZ106 | 4 |
| Costco | OmniBreeze | 5 |
| Daewoo | DDV200 | 4 |
| Deka | G3 Fan; MS16 | 15 |
| Dimplex | Mont Blanc dxmbcf | 5 |
| Domo | DO8149; Fan | 12 |
| Dreo | Cruiser Pro T1S Fan | 7 |
| Dyson | Fan HP04 | 12 |
| EasyHome | PFR40RT | 6 |
| Elmark | LMD-24 | 7 |
| Equatlon | Fan | 3 |
| Eton | NDY-DT2-Portable-Evaporative-Tower-Fan | 6 |
| Fakir | VC60 DC 2019; VC60 DC 2020 | 13 |
| Fust | Primotecq Tower Fan TF4201TR-L | 6 |
| Hatari | L1Fan | 5 |
| Hitachi | HEF RM1 Fan | 8 |
| Holmes | Fan HTF3610AR | 5 |
| Homezest | LWTF-04 | 5 |
| Honeywell | Fan C152; Fan CL18PE | 13 |
| Igenix | DF0035TBL | 5 |
| iLukodu | TFS-20RH | 4 |
| KDK | Fan K14X5 K14Y5; Fan KC4GR | 13 |
| Kinzo | LQ-AC02S Fan | 6 |
| Klarstein | Sommwerwind | 7 |
| KOENIC | KSFW 40322 TRC; KTFC 602722 | 13 |
| Lasko | Fan 2551 | 5 |
| LeaderPro | DC Motor Floor Fan; Desk Fan | 14 |
| Lutron | Maestro Fan | 5 |
| MainStays | Fan FZ10-19JR | 5 |
| MaxxAir | Airxcel | 9 |
| Meaco | Fan 1056 P | 9 |
| Mistral | MTFYG07R | 6 |
| Mitsubishi | R30J-HRV | 5 |
| Mycarbon | Fan | 6 |
| OmniBreeze | DC2018 | 5 |
| oneConcept | Hightower-2G | 5 |
| Orbrgozo | Orbegozo SF0243 | 4 |
| Ozeri | UltraWind OZF1 | 6 |
| Panasonic | FV-30BG1x | 23 |
| Pelonis | PFS40D6ABB Fan | 4 |
| Pepeo | Raja DC 122 Fan | 12 |
| Platinum | Home Fan | 2 |
| ProBreeze | PBF04 | 6 |
| Profi Care | Profi Care PC-VL 3069 | 6 |
| Quads | QS303 Fan | 8 |
| Rowenta | 5870; Mosquito Silence VU6410FD | 13 |
| Rubine | RCF-FORZA45-5BL-GM | 8 |
| Satellite Electronics | Fan | 6 |
| Sencor | SFN 5040BL | 6 |
| Senko | TR828 | 6 |
| ServilleClassics | SC-UltraSlimLine-Oscillating-Tower-Fan | 5 |
| Seville Classics | SC-UltraSlimLine-Oscillating-Tower-Fan | 5 |
| Shami | Fan-4333E | 5 |
| Shark | FlexBreeze-FA22X | 8 |
| SHE | Fan SHE40ST1801F | 4 |
| Shinic | BP69D | 5 |
| Siguro | fn v750w | 10 |
| Silver Crest | SilverCrest Fan | 5 |
| Solida | IFan14 | 8 |
| Solis | Easy Breezy | 5 |
| Somela | Wind Breeze 2 in 1 | 5 |
| Stadler Form | Stadler Form Peter | 5 |
| Taotronics | TF001; TT-TF009 | 13 |
| Taurus | Babel RC | 5 |
| TCP | Fan | 6 |
| TECHNIC | YH-32RA | 5 |
| Tefal | Essential VF3910F0 | 5 |
| Thomson | Fan THEVL484K | 4 |
| Toshiba | FWSA20 | 4 |
| U-Ultty | SKJ-CR021 | 7 |
| Ufesa | TW1700-OSLO-WIFI | 5 |
| Utilitech | Fan FZ10-19JR | 5 |
| Vitek | VT1907 | 6 |
| Vornado | 279TR; OSCR37 | 9 |
| Westinghouse | 78095 | 6 |
| Woozoo | Fan; SC15T | 20 |
| Xpelair | XPA360CF Fan | 5 |

## Iluminación — 113 marcas

| Marca | Modelos | Botones |
|---|---|---|
| ADJ | UC-IR | 19 |
| Airivo | NorthernLights | 21 |
| AIRUEEK Sonic LED Lamp | AIRUEEK Sonic LED Lamp | 12 |
| Amazon | LED Lights | 17 |
| American Lighting | American Lighting LED; American Lighting Neon Flex Light | 41 |
| Antizer Flameless Candles | Antizer Flameless Candles | 6 |
| Aoguerbe | BRSW04 | 24 |
| Arlec | LVX964 | 12 |
| Artika | Europa 21in chrome | 13 |
| AuraGlow | LED | 12 |
| AuraLED | ColorStrip | 23 |
| Baltimore | LED Light Strip | 14 |
| Bawoo | LED | 12 |
| BeamZ | ShowBar Laser; Skynight | 47 |
| BoomTone | DJ LEDPAR 7x10W 5in1 | 16 |
| Bower | ring 1 | 9 |
| Briloner | 14118006L; BN254818 | 35 |
| Brizled LED Icicle Lights | Brizled Christmas Icicle Lights | 19 |
| Cadrim | Puck Lights | 19 |
| Calex | LED strip | 18 |
| Cameo | CLPFLAT1REMOTE | 20 |
| CandleChoice LED | CandleChoice | 8 |
| Century | SRSOL509040 | 12 |
| Chauvet | DJ IRC6 | 26 |
| Combuh | COM-T070 | 6 |
| CooPark | LED | 24 |
| CountryField | LED Lamp | 2 |
| Dasinko | LED | 5 |
| Defiant | 17FL0102 | 6 |
| Deluxe Homeart | Deluxe Homeart FB-00001 | 8 |
| Duni | MP12ECOLOR | 21 |
| EAMBRITE | 100LT Fairy Lights | 35 |
| ECOLITE | LED Light ECOLITE HB333 | 7 |
| EGLO | light | 20 |
| Emerson | NH303UD | 32 |
| EVE | LED SOLAR PI1901 | 8 |
| Feron | AL5000 Starlight | 3 |
| FlarX | FlarX | 4 |
| Gako | RGB CONTROLLER 5050SMD | 24 |
| Globo | Connor Ceiling LED Lamp | 10 |
| Golden Power | Christmas lights Gp; GoldenPower XmasLights | 38 |
| Govee | Striplights; TV Backlights | 26 |
| HGomx | Saturn LED Lamp | 24 |
| HLKZine | Astronaut Star Projector | 12 |
| HomeMost LED | HomeMostLight | 10 |
| Hovvida | LED | 44 |
| Hxweiye | 300LED Fairy Curtain Lights | 17 |
| iDual | iDual; JEDI Lighting LED Light | 50 |
| ION | HolidayPartyPlus | 24 |
| ISY | UNKOWN | 24 |
| iTrekkings | Quechua Camping Lamp Click | 1 |
| Jialine Candle | 10-key Candle | 10 |
| JYSK | Kristjan LED candle | 8 |
| Keepsmile | Led lights | 14 |
| Kernowo | Solar LED Light | 6 |
| Kikilive | Jelly Lamp | 25 |
| Kiwong | 333SolarLedLight | 5 |
| KODA | LM030008-1 | 23 |
| Kohree | LED Lights | 10 |
| KooPower | hg4011 LED Light String | 12 |
| L8Star | LED Lights | 20 |
| Lethe | RGB LED; RGB LED full | 26 |
| LinahiLED | LINAHI NO 80 1200CH | 10 |
| LolaLED | LOLA LED | 2 |
| Lotti LED | Remote XT-RC1 | 2 |
| Lucoza | Candle1; Candle2 | 18 |
| Luminara | Candle | 10 |
| Luminarte | Luminarte | 11 |
| Mirage | Candle | 4 |
| Monster Basics | LED MONSTER BASICS; Monster LED Touch Light | 44 |
| MonsterIlluminessence | LED | 24 |
| Moredig | LED PROTECTOR | 8 |
| Msmkeji | Grow Light 100w | 27 |
| Neewer | nw 22b | 16 |
| Neuhaus | Leuchten Direkt; Leuchten Direkt LOLA | 20 |
| Nexillumi | LED | 22 |
| Noma | LED STRING LIGHTS | 32 |
| Northerns Light | Northerns Light Chain | 10 |
| OLLNY | HG29V212C; LED | 32 |
| ONN-Lighting | ONN LED | 43 |
| Oremi | sunset lamp | 24 |
| OShine | Flameles Candles | 10 |
| Osram | 24 Button LED | 24 |
| Ove | Strip Lights | 12 |
| Panasonic | HK9494 LED; PBUM11070 RGB Bulb 7w | 18 |
| Paulman | LED | 2 |
| Piaozoo | fairy lights | 24 |
| PixMob | main; special | 174 |
| Power Lighting | Power Lighting Meteor VI | 12 |
| Prolights | LUMIPARIRCH | 27 |
| Puck Button | Sooair Puck LED | 19 |
| Pumpkin Lights | Pumpkin Lights | 9 |
| QTX | led par | 16 |
| Rusta Christmas Tree Candle | Rusta Xmas Candle | 6 |
| RY King | RY King Flameless Candles | 10 |
| Sonoff | LED | 6 |
| Sunset Lamp | Sunset Lamp | 24 |
| Sylvania | 24 Button LED; LED | 40 |
| T-SUN | TiL LED 24W | 12 |
| TAO | A60BHV-RGB LightBulb | 24 |
| Tridonic | basicDIM DCG PROGRAMMER; REMOTECONTROL IR6 | 50 |
| tuffenough | all models | 5 |
| Tuya | Sunset LED Lamp | 12 |
| Tween | Light Oria | 11 |
| Tween lights | Tween light; Tzumi Auraled Colorstrip 12ft | 30 |
| Tzumi | AuraLED | 24 |
| UFO Sunset Lamp | UFO Sunset Lamp | 8 |
| Unknown | 44 LED Strip; LEDStrip | 134 |
| V-TAC | VT5050 LED Strip | 9 |
| Varilux | Lightme LED Bulb | 24 |
| Veezio | Light Bulb | 19 |
| Warmoon | RGB | 10 |
| Xtreme Lit LEDs | XTREME 3 2FT LED strip | 15 |

## Otros equipos (cajas, monitores, cámaras…) — 318 marcas

| Marca | Modelos | Botones |
|---|---|---|
| 20Q Challenge | 20Q Challenge PoV game display | 7 |
| 3D Clock | 3d clock color | 21 |
| 3M | Air Purifier | 3 |
| Acorn | Stairlift | 2 |
| Advanced FM Car Transmitter | Advance FM Transmit | 20 |
| AEG Cooker Hood Haube | AEG Haube Cooker Hood | 4 |
| Aigo | config01 | 1 |
| AltraFlame | FIREPLACE | 5 |
| Amazon | Alexa Voice 2ndGen | 4 |
| Amerlife | Fireplace | 6 |
| AMI | Jukebox Full | 17 |
| Amibot | spirit | 10 |
| Amino | Amigo | 11 |
| Andonstar | AD206 Microscope | 20 |
| AOC | JXD 021 | 24 |
| Aorus | FV43U | 12 |
| Apple | Remote Gen2; TV 4K Gen2 | 17 |
| Arcadyan | HMB2260 | 33 |
| ArgonOne | Argon IR | 11 |
| Arizer | Extreme Q | 18 |
| Arlec | CH226 | 8 |
| ARRIS | Arris | 53 |
| Ashley Furniture | Ashley Furniture W100-101 | 7 |
| Asus | OPlay | 23 |
| Atlas | URC1056 | 55 |
| AVer | COMVREMOT | 28 |
| AVer Document Camera | AVer RM-N6 Document Cam | 24 |
| Avfabul | avstar hdmi splitter switch | 3 |
| Azulle | Byte4 Fanless MiniPC | 11 |
| Bell | Sat CAN | 44 |
| Benchmark | DAC2 HGC | 14 |
| BenQ | EX2780Q; RE7501 Interactive Display | 19 |
| Berbel Kitchen Hoods | Berbel Skyline 2011 | 9 |
| Bio | Bidet USPA 6800 | 1 |
| BioBidet | bb2000 | 17 |
| biOrb | Classic 15 Aquarium | 28 |
| Blueair | T10i T20i | 8 |
| Bluetooth Amplifier | OE-BT V2.0 Amp | 21 |
| Blyss | ZE-82 | 5 |
| Bose | Videobar | 13 |
| BOUYGUES | Bbox 4K | 33 |
| BoxLight | Interactive Flat Panel PROCOLOR653U | 31 |
| Boxlight | Smartboard | 15 |
| Bqeel | R1-Plus | 9 |
| Brand Unknown | Rgb magic lighting | 24 |
| Brandell | DR802 | 10 |
| BrandUnknown | CCTV DVR | 30 |
| Brio | Travel Train | 10 |
| Bulsatcom | DVBTV | 12 |
| Cabelas | Gun1 | 2 |
| Cahors | TVS 7900HD TNT Sat | 47 |
| CAME | security barrier i100 programming | 33 |
| Canon | RC-6 Trigger | 1 |
| Chunghop | RM 88E Universal | 38 |
| Cisco | CTS-RMT-TRC6 | 23 |
| Citysports | WP2 | 3 |
| Claro | Fujian Newland TV Box | 32 |
| ClassicFlame | Spectrafire | 6 |
| Cleartouch | Cleartouch | 28 |
| Clever&Clean | AQUA-Series 03 | 8 |
| Clevertouch | Clevertouch | 52 |
| Clinton Electronics | Clinton Electronics M-10A | 15 |
| Cogeco | Cable Box CAN | 25 |
| Comcast | Xfinity XiOneSC-b | 5 |
| ComfortZone | Heater | 4 |
| ConnectPro | KVM | 12 |
| Contemporary Research | Contemporary research | 35 |
| Coretec | TRAINER 1000 | 5 |
| Cox | Cisco DTA250HD | 25 |
| Ctouch | Ctouch | 10 |
| Cyfrowy Polsat | Cyfrowy polsat | 38 |
| dBot | W100 | 10 |
| Death Star Tree Topper | Death Star Topper | 1 |
| Deerma | DEM-LD6 | 4 |
| Delmar | Fireplace | 9 |
| DeLonghi | heater | 6 |
| Devola | DVPH1200B  Patio Heater | 10 |
| Diffuser | Homedics ARM-720A | 6 |
| Digi Day | Digi Day 3 | 16 |
| Dimplex | Electric Fireplace | 10 |
| Diprogress | dvb t | 38 |
| DIRECTV | Direct TV RC65 | 42 |
| Domoova | DRV70 | 9 |
| Dr Heater | Dr heater infrared | 5 |
| Dreo | Heater | 7 |
| DSTV | EXPLORER | 39 |
| Dualtron Victor Handlebar Lights | Dualtron Victor Handlebar Lights | 5 |
| DuneHD | DuneHD | 49 |
| Duraflame | Heater | 4 |
| DynaScan | DS2 | 10 |
| Dyson | Purifier Humidify Cool Formaldehyde; PurifiyHumidify PH04 | 25 |
| Eakins | 48MP | 16 |
| Ecovac RC1507 Robotics | Ecovacs RC1507 | 12 |
| Ecovacs | Deebot DM82 | 10 |
| EdenPURE | RPE1500 A5093 | 4 |
| Eichhorn | Train | 5 |
| ElecHome | SH8820 Humid | 4 |
| Elisa Viihde | Elisa viihde | 14 |
| Elune Vision Motorized Projector Screen | Elune Vision Motorized Projector Scr | 3 |
| Emotiva XDA-2 PreAmp | Emotiva XDA-2 | 13 |
| Enido | LED Candle | 10 |
| EsyLux | em10425509 | 28 |
| Eufy | 30C; RoboVac 11S | 24 |
| Eurolan | TH100 | 44 |
| EVO | 4 | 40 |
| Evolution | 4x1 HDMI Switch; DTA-Cable DM8-1002-CA | 49 |
| Extron | IR903 | 29 |
| Fantec | 4KS5700 | 34 |
| Fisher Price | FISHERPRICE CDN41 | 1 |
| FlameMore | Heater | 3 |
| Formuler | Z8 | 36 |
| FOXTEL | IQ2-IQ3 | 41 |
| FranSat | NewSat Maestro | 35 |
| Frico | Air Curtain | 3 |
| Fujifilm | config01 | 1 |
| G-Story | Monitor | 10 |
| Gakken | WorldEye | 17 |
| GANXIN | GXIR05 | 33 |
| General Electric | ge range hood | 12 |
| Get | Mini | 33 |
| Gold Lough LED Color Changing Ball | Gold Lough Color Changing Ball | 18 |
| GoldenVantage | AF510 Fireplace | 4 |
| Goofy Htwon | Goofy Htwon | 9 |
| Gym HIIT | GX-IR03 Clock | 48 |
| Haier | config01 | 1 |
| Hallmark | Hogwarts Castle Tree Topper | 1 |
| Hampton Bay | EST-540T-40R | 1 |
| Hasbro | Furby | 14 |
| Hayear | HY-5200 Microscope Camera | 18 |
| HexBug Remote | HexBug Remote CH-A | 4 |
| Hieneking Media | Hieneking Media Schule | 9 |
| Hikvision | Hikvision | 39 |
| HIPHOPTOY | HipHopToy | 4 |
| Hob2Hood | hob2hood | 7 |
| HOBOT | 2S | 12 |
| Holmes | Heater | 5 |
| Homday | Homday | 4 |
| Homedics | ARM-720A Ellia Essential Diffuser | 6 |
| Honeywell | HCE323V | 7 |
| Huawei | dn300 mgts(mts) | 24 |
| Humax | generic cable box | 7 |
| i3-Technologies | i3touch | 36 |
| iCLEBO | Omega | 15 |
| iHome | Nova; Rz6 | 26 |
| iLife | A4s Vacuum | 8 |
| IQAir | GC Series | 10 |
| iRobot | Roomba 600 | 8 |
| Jamara | 410065 Gun | 4 |
| JB Systems | COB-4BAR | 2 |
| Jet | ASF1000B | 3 |
| JETTools | AFS-1000B Air Filter | 3 |
| Jukebox | Rock ola CD Jukebox | 27 |
| K-Line | COCACOLA TRAIN | 5 |
| Kaleidescape | Strato V | 15 |
| KidLand | train | 5 |
| Kinivo | 301BN | 5 |
| Klarstein | Kamin | 5 |
| Kodak | EasyShare Max Z990 | 1 |
| Konwin | GD921BCP 4JR Heater | 6 |
| KopBeauAireplus | Digital Oil Filled Heater | 5 |
| LaserX | Tag | 3 |
| Lasko | CC23630 | 10 |
| Legamaster | DIS-9800 | 16 |
| LEGO | Mindstorms RCX | 16 |
| Leicke | KANAAN-KN40573 | 20 |
| Lenson | LD2500 | 49 |
| Lervia | hnc 2500e | 7 |
| Levoit | Humidifier | 7 |
| LG | AKB74915384; Hom-Bot Vacuum; LG; UltraGear OLED Monitor | 98 |
| Lifesize | Icon vid cnf | 6 |
| Lifesmart | 208220 | 6 |
| Ligawo | HDMI 6518716 | 12 |
| LightMyBricks | lightmybricks | 9 |
| Logitech | BCC950; RI0019 for PTZ Pro 2; Squeezebox X-ia4 815-000058 | 54 |
| Loncevon | 12 inch Small Monitor | 17 |
| LongHua | WCF28 | 7 |
| Lutron | Architectural Lighting | 20 |
| Maerklin | Marklin My World-Junior | 14 |
| MAG | TV Box | 28 |
| Mag | TVbox Remote | 38 |
| Magnavox | ZV450MW8 | 46 |
| Makeblock | generic | 21 |
| Manhattan | SX FREESAT | 34 |
| MarshallElectronics | Marshall CV610 | 18 |
| Maxcan | mh 602brc | 6 |
| Mecool | KM6 | 12 |
| Meo | Cable Box | 5 |
| Micca | N10; SpeckG2 | 32 |
| Microsoft | Xbox One Media Remote | 37 |
| Mini Laser Lightshow | Mini Laser Lightshow A05 | 11 |
| Mirage | DR5-PVR | 42 |
| ModernEmber | Fireplace | 10 |
| Monoprice | 43305 | 16 |
| MonoPrice | HDMI-Switch | 8 |
| MovinFlame | LED Candle | 2 |
| Mrocioa | HDMI SWITCH | 5 |
| MSI | DigiVox MiniII | 19 |
| MT-VIKI | MT-HD0808 | 31 |
| Muskoka | MHC35BL | 5 |
| Nedis | CATRBK100 | 20 |
| Nerf | LaserOps PRO | 3 |
| Netgem | 7700 Cable Box | 34 |
| Newline | Full Remote | 42 |
| Nexigo | N950P Webcam | 14 |
| Nikon | Nikon | 1 |
| Nixplay | Picture Frame | 11 |
| Nixplay Digital Frame | Nixplay Seed Digital Frame | 9 |
| Northern | NTH-LED24R | 6 |
| Octagon | generic cable box; SF8008 | 46 |
| Olympus | config01 | 1 |
| Onescreen | Onescreen | 56 |
| onn | 4K Pro | 20 |
| Orange | Cable Box | 10 |
| Orbegozo | RRE 1310 A | 4 |
| Ordro | config01 | 1 |
| OREI HDMI Switcher | OREI HD-401MV | 16 |
| OSSC | Pro | 51 |
| Panasonic | DPVF1615ZA; N2QAEC000017 MiniDV remote | 49 |
| Pandigital | DPF70-2 | 11 |
| Pentax | config01 | 1 |
| Philips | swv9484b hdmi switch; Vacuum Philips FC8795 | 14 |
| PlayStation | 2 | 50 |
| Playtive | Toy Train | 10 |
| Pleasant Hearth | Pleasant Hearth | 3 |
| Polycom | VX series remote | 40 |
| Prime3 | Air Purifier | 8 |
| ProBreeze | ProBreese PB F13B US Dehumid | 8 |
| Promethean | Digital Whiteboard | 14 |
| Prowise | Proline | 16 |
| Prozor | HDMI Switch | 9 |
| PTZOptics | NDI HX CAM1 | 36 |
| Pyle | Amp PREA90WBT | 10 |
| Qlima | Humidifier | 5 |
| QNAP | RM IR2 | 13 |
| Quasar | 41101445 | 28 |
| Rabbit Air | Rabbit Air Minusa2 | 5 |
| RadioShack | 1500474 | 9 |
| Retro-Bit | Prism | 6 |
| RetroTINK | 5X Prem | 54 |
| Rinnai | SpaceHeater | 4 |
| RME | ADI-2 DAC FS | 22 |
| RoboMaster Blaster | Robomaster Blaster | 1 |
| Robot | RC Smart Dance Robot | 15 |
| RocketFish | RF G1501 | 5 |
| Rohnson | R-9400 | 4 |
| Roksan | cd player | 33 |
| Roku | 2 channels | 29 |
| Roland | SC55 SB55 | 19 |
| Rolsen | RTH-2778RT | 7 |
| Roomba | remote | 13 |
| Rostelecom | Wink STB | 30 |
| RoyalFlame | Vision 42 LED | 9 |
| Rytaki | US HDMI501-8K-H | 8 |
| Samsung | SR8875; Ventilator ERV | 20 |
| Sanyo | config01 | 1 |
| Sencor | SFH 9013 | 4 |
| Seville | Classics EHF10119PB Fan | 5 |
| Shark | Air Purifier | 8 |
| Sharp | signage | 11 |
| Siemens | LR96CAQ20 | 10 |
| SilverCrest | SKD 2300 D4 | 7 |
| Silverlit | Yoco Robocombat blue | 5 |
| Simon | 75350 69 | 16 |
| Simulflame | LED Candles | 8 |
| SkyQ | SamsungTV | 5 |
| SMART Technologies | SMART Board MX | 16 |
| SmartyKat | Mouse RC | 2 |
| Socobeta | VGAtoSCART | 9 |
| Sony | Sony | 23 |
| Sony Ericsson | SE MRC-60 | 8 |
| Sony Experimental unlock | Blu 1633 sirc20 | 11 |
| Starcom | 9947 HD | 45 |
| Stereo Amplifiers | Dexon JPM | 19 |
| Storex | MediaZapperHD | 44 |
| Style Selections | Style Selections Electric Fireplace-KDI-01 | 5 |
| Tandberg | TRC V iTV | 34 |
| TechniSat | SAT1 | 26 |
| Telus | OptikTV | 30 |
| Tesla | RoboStar T80 Pro; TE-300 | 30 |
| TESmart | KVM | 8 |
| Theta | GENVIIIS3 | 14 |
| Thinkway Toys | Wall-E Robot | 10 |
| Thomson | THT501 | 46 |
| Tikom | G8000 | 11 |
| Tilview | DVB-T 2 | 48 |
| Toilet Roll Dispenser | Roll | 1 |
| Tomlov | DM201 Digital Microscope | 3 |
| Tomlov Microscope | TOMLOV DM9 MICROSCOPE | 6 |
| Topcon | PSR-2000 vision test remote | 41 |
| Topping | DX3 Pro Plus-Amplifier | 13 |
| Topping Amplifier | Topping DX3 Pro-Amplifier | 13 |
| Toys R Us | Toys R Us Musical Train | 3 |
| Tripp Lite | Tripp Lite B005-HUA4 | 6 |
| Trotec | AirgoClean 350 E | 10 |
| TrueConf | CleverMic1011 | 27 |
| TwinStar | Twin Star Fireplace | 7 |
| Ugreen | 8k hdmi switch | 4 |
| Uncle Milton | Moon in my Room | 3 |
| Uncle Miltons | SolarSystemInMyRoom | 1 |
| Unknown Brand | Flame humidifier | 5 |
| Unknown-brand | Solar system | 3 |
| Urevo | Treadmill UR9TM0011 | 3 |
| Vactidy | T6 | 11 |
| Varytech | Fog Machine F80B | 2 |
| VibroShaper | VibroLegs | 8 |
| ViewSonic | ViewBoard | 19 |
| Vornado | Heater AWRH | 5 |
| WayneDresser | GasPump Remote | 16 |
| WEN | 3417 | 3 |
| Whalen Furniture | HDFP86-47 | 6 |
| Winix | Air Filter; Air Purifiers | 10 |
| Winyea | Cyberpunk WinyeaTag LaserTag | 28 |
| WowWee-RoboSapien | RoboSapien | 60 |
| Xfinity | XR11 | 40 |
| Xflyee | Wallclock All Models | 15 |
| Xiaomi | MiBox1 | 11 |
| Yealink | UVC86 | 14 |
| Ziglint | RobotVaccum | 11 |

---

## Cómo se regenera esta lista

```bash
python3 tools/ir_catalog/build_catalog.py --irdb /ruta/Flipper-IRDB --report docs/marcas_modelos.tsv
```

El script lee Flipper-IRDB (CC0 1.0), normaliza cada señal al motor IR de la app y regenera el asset. Al actualizar IRDB basta con volver a ejecutarlo y subir `AssetCatalog.VERSION` para que la app reimporte.
