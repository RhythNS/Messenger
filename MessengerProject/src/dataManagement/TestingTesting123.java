package dataManagement;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Scanner;

public class TestingTesting123 {

	private static String[] names = { "Gnasher", "Mei", "Tracer", "Stopfer", "Azeleia", "Rhyth", "Littlepip",
			"Black Saddle", "Calamity", "Velvet Remedy", "Benär", "Steelhooves", "Xenith", "Red Eye", "Gawd", "Stern",
			"Xephos", "Blackjack", "Morning Glory", "Hired Gun", "Puppysmiles", "P-21", "Scotch Tape", "Hard Cider",
			"Palette", "Railright", "Apple Whiskey", "Crane", "Candi", "Barrel Cactus", "Turquoise", "Trolley",
			"Kage Grimfeathers", "Regina Grimfeathers", "Blackwing", "Butcher", "Mister Topaz", "Deadeyes", "Scramble",
			"GrimStar", "DJ Pon3", "Homage", "Helpinghoof", "Life Bloom", "Monterey Jack", "Blueberry Sabre",
			"Cottage Cheese", "Nova Rage", "Poppyseed", "Strawberry Lemonade", "Doctor Glue", "Doc Slaughter", "Blood",
			"Daff", "Sandy Shores", "Radar", "Cracker", "Cager", "Sawed Off", "Star Sparkle", "Caliber", "Lionherat",
			"Firestar", "Mouse", "Autumm Leaf", "Pride", "Windsheer", "Watcher", "Jokeblue", "Silver Bell", "Memory",
			"Preacher", "Twilight Sparkle", "Rarity", "Rainbow Dash", "Fluttershy", "Applejack", "Pinkie Pie",
			"Apple Bloom", "Sweetie Belle", "Scootaloo", "Zecora", "Zynthia", "Trixie", "Gilda", "Angel", "Death",
			"Silver Spoon", "DiamondTiara", "Snips", "Snails", "Midnight Sparkle", "Quanta", "Gestalt", "Mosaic",
			"Vinyl Scratch", "Discord", "Rampage", "Lacunae", "Boo", "Stygius", "Psychoshy", "Psychodash", "Xanthe",
			"Daisy", "Marmalade", "Midnight", "Gin Rummy", "Rivets", "Duct Tape", "Charity", "Priest", "Sekashi",
			"Majina", "Boing", "Scoodle", "Busted Heart", "Reimu", "King Lui", "Strom Front", "Bluebelle", "Baby Blue",
			"Dazzle", "Diamond Flash", "Big Daddy", "Brutus", "Cuffs", "Mallet", "Smokey", "Crunchy Carrots",
			"Stronghoof", "Knight Crumpets", "Bottlecap", "Caprice", "Usury", "Aries", "AquariusZodiac", "Dr.Zodiac",
			"Silver Stripe", "Gemini", "Leo", "Cleove", "Capricorn", "Dusk", "Lightning Dance", "Sky Striker", "Dawn",
			"MoonShadow", "Lambent", "Chicanery", "Professor Morningstar", "Twister", "Boomer", "Sunset",
			"Storm Chaser", "Sanguine", "Deus", "Gorgon", "Brass", "Fury", "Lancer", "Legate", "Lighthooves",
			"Steel Rain", "Abod", "Adalbeort", "Adalgar", "Adham", "Adken", "Adulfuns", "Aelf", "Aelfraid", "Aelfric",
			"Aelor", "Aescby", "Aethel", "Aethelberht", "Aethelisdun", "Ahanor", "Aherne", "Ahrin", "Aidan", "Aidtun",
			"Aifrid", "Ailean", "Aimil", "Aineislis", "Arileas", "Aislinn", "Alain", "Albhaois", "Albion", "Aldus",
			"Aler", "Algonthir", "Alraed", "Alhric", "Alhwin", "Alian", "Allsun", "Alviss", "Amalasand", "Amalien",
			"Amario", "Amber", "Amhiunn", "Amhlaidh", "Amires", "Amlauril", "Amon", "Anant", "Anaurathiel", "Andariel",
			"Andarius", "Anfalas", "Anhlaoigh", "Anntoin", "Anwyl", "Aodh", "Aodha", "Aodhagan", "Aodhan", "Aoidh",
			"Aoiffe", "Aonghus", "Aralian", "Aralt", "Arela", "Arheyu", "Arndell", "Arnhold", "Arni", "Arnwald",
			"Arnwulf", "Arombolosch", "Arregaithel", "Artair", "Arthwr", "Arthylomis", "Artur", "Asgault", "Athàlùsa",
			"Athdara", "Athdara", "Attewelle", "Avis", "Awurin", "Aylen", "Baehloew", "Bagon", "Bain", "Bairghith",
			"Baldmar", "Banain", "Banbrigge", "Bangan", "Banlòr", "Banurr", "Bardawulf", "Bardhardt", "Bargash",
			"Barghan", "Barthr", "Beadu", "Beagan", "Bearach", "Beathag", "Bebhinn", "Becere", "Beledene", "Beonetleah",
			"Beorc", "Beordtraed", "Beorht", "Beorhthram", "Beormann", "Beornet", "Beorttun", "Beorwalt", "Berchtwald",
			"Bercleah", "Berdine", "Berin", "Berinhardt", "Bhaird", "Bhaltair", "Bhaltair", "Bhragas", "Binge", "Binok",
			"Binokee", "Blaecleah", "Blaed", "Blar", "Bliths", "Bloddwyn", "Blotsm", "Bluainach", "Boda", "Bofind",
			"Bofind", "Bogohardt", "Boltar", "Born", "Boron", "Bothi", "Boyne", "Bradach", "Brangwen", "Brann",
			"Breandan", "Bret", "Brian", "Bridhid", "Brock", "Bronwyn", "Broth", "Bryn", "Brys", "Buadhach", "Buidhe",
			"Burgal", "Burr", "Cadawig", "Caddrairc", "Cadel", "Cadhla", "Caellach", "Caerau", "Caerghallan", "Cai",
			"Cailean", "Caileass", "Cain", "Caitlin", "Calldwr", "Cambeul", "Cameron", "Canshron", "Cant", "Caoinleain",
			"Caolabhuinn", "Caolaidhe", "Caomh", "Caomhan", "Caomhiun", "Caradoc", "Caramichil", "Cariadland",
			"Carleas", "Carriag", "Carridin", "Casidhe", "Cassimir", "Cathan", "Cathaoirmor", "Cathasach", "Cathmaol",
			"Ceallach", "Ceannfhionn", "Ceara", "Cearbhallain", "Cearnach", "Cearrbhach", "Ceileachan", "Cein",
			"Cellanir", "Ceneric", "Ceran", "Chalice", "Chandiris", "Charea", "Cianan", "Ciarda", "Cillcumhan",
			"Cillin", "Cinfhaolaidh", "Cingesleah", "Cinnard", "Cinneididh", "Cinnfhail", "Ciulthinn", "Claefer",
			"Claeg", "Cleve", "Clif", "Clywd", "Coal", "Coalan", "Coed", "Coilin", "C", "oille", "Coinneach", "Coire",
			"Conaire", "Conan", "Conn", "Conndchadh", "Corbmac", "Corcurachan", "Corelja", "Corondal", "Corondhal",
			"Corzar", "Craccas", "Creag", "Creaga", "Creiddylad", "Creya", "Cristin", "Cuinn", "Curadhan", "Cuthbeorht",
			"Cwen", "Cwladys", "Cynbel", "Cyne", "Cyneburhleah", "Cyneric", "Cynesige", "Cyrius", "Cythranil",
			"Daegelbeorht", "Daegeseage", "Dael", "Daeltun", "Daeran", "Daghat", "Dagian", "Dagomar", "Dagr", "Daimhin",
			"Dalach", "Dalr", "Dalyell", "Danr", "Daregas", "Darhan", "Dariel", "Darwyn", "Dearan", "Deardrui",
			"Deasach", "Deasmumhan", "Debroun", "Defyaio", "Delair", "Dellingr", "Demandred", "Demyavan", "Dene",
			"Denethor", "Denu", "Deorward", "Dercarat", "Derenai", "Derylynn", "Dewi", "Dewi", "Diamar", "Diarmaoid",
			"Dikibyr", "Diolmhain", "Diomassach", "Direa", "Diss", "Doghailen", "Dogrim", "Doire", "Doireann",
			"Domhnull", "Dorminil", "Draca", "Drugiself", "Dryw", "Dseoran", "Du", "Duana", "Dubh", "Dubhgan",
			"Dubhghall", "Dubhglas", "Dubhlachan", "Dubhloach", "Dubhthach", "Duddaleah", "Dufrhealh", "Duhlasar",
			"Dumond", "Dunleah", "Dunn", "Dyddplentyn", "Dylan", "Dylan", "Eachan", "Eachthighearn", "Eada",
			"Eadbeorht", "Eadgar", "Eadmund", "Eadwulf", "Ealadhach", "Ealdraed", "Ealhard", "Ealhdun", "Eamon",
			"Eanruig", "Earnest", "Earric", "Eathelin", "Eatun", "Eberk", "Eburhardt", "Ecgbeorth", "Eferhard",
			"Efrania", "Ehren", "Eibhlin", "Eideann", "Eilis", "Einher", "Einion", "Eiric ", "Eirik", "Eister",
			"Elanear", "Eldrias", "Elemthain", "Ellinar", "Elram", "Elrias", "Elspe", "Elsurion", "Endover",
			"Engelbergt", "Engholm", "Enit", "Eodoaine", "Eoghan", "Eoin", "Eorforwic", "Eorl", "Eostre", "Erinn",
			"Erminric", "Ertha", "Estcot", "Esthandir", "Esyathol", "Ethiyanil", "Eyrekr", "Eysellt", "Faegan",
			"Faeroth", "Faerrleah", "Faerven", "Faerwald", "Fairhinath", "Famek", "Faodhagan", "Fearbhirigh",
			"Fearghal", "Fearghus", "Fearn", "Feich", "Felabeorht", "Felizitas", "Fender", "Feoras", "Fiamar",
			"Filmaen", "Fingolfin", "Fionn", "Fionnghalac", "Fionnghuala", "Fips", "Firlionel", "Flanna", "Fleotig",
			"Floinn", "Flynt", "Fridu", "Friduric", "Frimunt", "Fugentun", "Gaelan", "Gaelbhan", "Galchobhar",
			"Gallgaidheal", "Gandalf", "Garisin", "Garivou", "Garm", "Garthr", "Garwig", "Geatan", "Genji", "Gerhwas",
			"Gerrod", "Gerwalt", "Ghleanna", "Gilolla", "Gimli", "Giollamhuire", "Giollaruaidh", "Gionnan", "Giorsal",
			"Gipcyan", "Gislbyr", "Gled", "Glenndun", "Glynydd", "Gnarf", "Gnimsch", "Gnosch", "Goathaire", "Goda",
			"Godehard", "Godgifu", "Gondo", "Goridh", "Goridh", "Gorman", "Gorman", "Goscelin", "Gothfraidh", "Grada",
			"Graegleah", "Griswald", "Gruffudd", "Gunnhar", "Guthr", "Gwalchmai", "Gwendolyn", "Gwenhwyvar", "Gwlsdys",
			"Gyldan", "Gyrwode", "Gytha", "Gyvron", "Hacor", "Hadu", "Haele", "Haesel", "Haestibgas", "Hafirinm",
			"Hafleikr", "Haga", "Hakon", "Halag", "Halfdan", "Halifrid", "Halig", "Haltor", "Hammar", "Hanraoi",
			"Haorinas", "Harad", "Haragraf", "Harailt", "H", "arpo", "Harti", "Haruald", "Hearpere", "Heathleah",
			"Heimrik", "Heort", "Heriberaht", "Herimann", "Herwig", "Hidlimar", "Hilbrand", "Hildhard", "Hohberht",
			"Hoibeard", "Hoireabard", "Holda", "Honod", "Howel", "Howel", "Hugiberaht", "Hugiet", "Hunfrid", "Hunig",
			"Iaian", "Ifig", "Iltak", "Imrahil", "Ingmar", "Iniadea", "Inis", "Iosep", "Isan", "Isedria", "Isenham",
			"Itu", "Ivhar", "Jami", "Jander", "Jaral", "Jeffries", "Jezer", "Joreg", "Jozan", "Kaja", "Kandorys",
			"Kerwyn", "Kiarr", "Kief", "Kiollsig", "Kirkja", "Kirkjabyr", "Knut", "Kort", "Korulas", "Krak", "Krossbyr",
			"Kuambyr", "Kulbari", "Kunagnos", "Kuonraed", "Kyan", "Kythauriel", "Labhruinn", "Ladhaoise", "Laec",
			"Lagan", "Laghras", "Laird", "Landbercht", "Langr", "Laochailan", "Laudrius", "Leagorn", "Leamhnach",
			"Leander", "Leannan", "Leathlaghra", "Lebennin", "Lefael", "Leif", "Leoma", "Leraneal", "Leschko", "Leskoh",
			"Lethanon", "Leutpald", "Lilias", "Lind", "Lindael", "Lindberg", "Lintflas", "Lioslaith", "Liusadh",
			"Llwyd", "Llyn", "Llyweilun", "Logmann", "Lokti", "Lomarin", "Lonn", "Lothar", "Lotharingen", "Lubig",
			"Lughaidh", "Lughaidh", "Luighseacg", "Luisadh", "Lundr", "Luthais", "Lyrandis", "Lyrsil", "Lysil",
			"Lysira", "Maarkan", "Mab", "Macothiel", "Madelhari", "Maegth", "Maeva", "Magafeld", "Magnus", "Maible",
			"Maighdlin", "Maire", "Mairghread", "Mairi", "Maithilis", "Mandel", "Mannfrith", "Maodighomhnaigh",
			"Maolmin", "Maolmin", "Maolmuire", "Maoltuile", "Marcail", "Maredud", "Mari", "Maril", "Marla", "Maskol",
			"Maura", "Maureen", "Meadhbh", "Mearr", "Meginhardt", "Meliondor", "Meredydd", "Merehloew", "Mersc",
			"Messkir", "Metira", "Metrios", "Mhari", "Mialee", "Micheil", "Minarvos", "Minata", "Mirtek", "Miureall",
			"Modread", "Mog-Macha", "Moibeal", "Moineruadh", "Moineruadh", "Moire", "Moireach", "Moldrack", "Monca",
			"Morag", "Morcan", "Morfinn", "Morgant", "Morgen", "Morogh", "Mortun", "Moya", "Muir", "Muire",
			"Muireadhaigh", "Muirgheal", "Muirne", "Murchadh", "Murthuile", "Mylnburne", "Naheniel", "Nathondal",
			"Naul", "Neblehle", "Nerviar", "Newyddllyn", "Niaeha", "Niall", "Nichus", "Niewheall", "Norberaht",
			"Nuallan", "Odbert", "Odharait", "Odhrean", "Odimorr", "Odwulf", "Oleifr", "Ollaneg", "Olvaerr", "Omid",
			"Oona", "Oonagh", "Ordalf", "Orharikr", "Osbeorht", "Oskar", "Osmaer", "Osraed", "Osric", "Othomann",
			"Owein", "Owein", "Padraig", "Padriac", "Paduicg", "Parlan", "Parlan", "Peadair", "Peadar", "Pennleah",
			"Peppi", "Perin", "Permeyah", "Preostleah", "Quarz", "Radagast", "Rafmag", "Allweg", "Ragdal ", "El Zoreh",
			"Raghallach", "Raghnall", "Raginmund", "Rahn", "Raiola", "Raja", "Ramiris", "Randwulf", "Raoghnait",
			"Raskogr", "Rauthuellir", "Raymir", "Readwulf", "Regaf", "Regdar", "Reginberaht", "Reidhachadh",
			"Rhinfflew", "Rhuk", "Rhydag", "Rhys", "Riagan", "Rian", "Ridere", "Rikar", "Rille", "Riocard", "Riodhr",
			"Rioghbhardan", "Rioghnach", "Rodhlann", "Rognuald", "Rois", "Ronan", "Rotland", "Ruadhan", "Ruarc",
			"Rudrik", "Rudugeard", "Rumenea", "Ruodger", "Ruodlant", "Ruomhildr", "Rurik", "Sadhbh", "Sadhbha",
			"Saegar", "Saelec", "Saerfren", "Saeweard", "Saidhghin", "Sailbheastar", "Saitham", "Sala", "Salaidh",
			"Salasu", "San Rhaal", "Saphir", "Saretus", "Sargas", "Saxon", "Scanlan", "Sceaphierde", "Scelfleah",
			"Schiraljie", "Scirwode", "Scolaighe", "Scrileadh", "Seadaidh", "Seain", "Seanachan", "Seanan", "Seanlaoch",
			"Seann", "Secgleah", "Seiradan", "Selvagitas", "Sentaia", "Sgeulaiche", "Sha Rell", "Sha'Red", "Shane",
			"Shauir", "Sibeal", "Siddael", "Sigifrith", "Sigilwig", "Sigimund", "Sigiwald", "Signi", "Sigurdhr",
			"Silanay", "Silmalinnon", "Silmarilon", "Silviara", "Sim", "Sindira", "Sine", "Siobhan", "Siodhachan",
			"Siolta", "Siomonn", "Sion", "Sith`e`thak", "Siubhan", "Siudhne", "Siusan", "Skentha", "Skereye", "Skorag",
			"Skypr", "Slaedr", "Slaghan", "Sliaghin", "Solamh", "Somahirle", "Sorcha", "Sruthair", "Sruthan", "Stanach",
			"Steorra", "Stodhierde", "Strom", "Sucram", "Suileabhan", "Suthrland", "Swynedd", "Tabbert", "Tad", "Taffy",
			"Taithleach", "Tamnais", "Taran", "Taurelias", "Tearlach", "Teimhnean", "Temara", "Tendrik", "Tespius",
			"Tewdwr", "Thalion", "Thamios", "Tharimis", "Thegn", "Theuobald", "Theuroik", "Thoidgeirford", "Thoraths",
			"Thorbiartr", "Thorbiorn", "Thorfin", "Thorir", "Thoud", "Throaldr", "Thruhleow", "Thrythwig", "Ti'ak",
			"Tighearnach", "Tioboid", "Tiomoid", "Tirell", "Togtar", "Toirdealbach", "Toireasa", "Tomas", "Torc",
			"Tordek", "Torm", "Tormaigh", "Torr", "Torra", "Tosdramos", "Trahayarn", "Tramiel", "Trea", "Treabhar",
			"Treasach", "Trekarraz", "Trent", "Trevelian", "Trystan", "Tsoladin", "Tuathal", "Turgal", "Txorass",
			"Tygr", "Tyrion", "Ualtar", "Udo", "Uigboern", "Uilleam", "Uinsionn", "Ulbon", "Ulfmaerr", "Ulvelaik",
			"Unnurr", "Vaasa", "Valadenya", "Valerius", "Varin", "Varvia", "Vollmr", "Vychan", "Wace", "Waenwryht",
			"Waescburne", "Waldramm", "Walijan", "Wallihelm", "Wandi", "Wann", "Waren", "Warto", "Wendido", "Wenis",
			"Werro", "Wigis", "Willaperht", "Willimod", "Winiholdo", "Wolf", "Wudoreafa", "Wulfgar", "Wulfric",
			"Wulfrith", "Wyrduàn", "Yaligan", "Yarrik", "YaYarzar", "Yedda", "Yofenia", "Zaasz", "Zareius", "Zarrag",
			"Zolt", "Abvia", "Adalheit", "Aeldra", "Aelfdene", "Aeltra", "Aemete", "Aethelmaere", "Aidan", "Ailin",
			"Aimil", "Aine", "Airleas", "Aislinn", "Alain", "Alaria", "Allsun", "Alundra", "Alviss", "Amhiunn",
			"Andaria", "Aoiffe", "Astryd", "Athalindi", "Attheneldre", "Aylen", "Baduhildi", "Baldwine", "Banbrigge",
			"Beathag", "Bebhinn", "Beorhthildi", "Berahta", "Berangari", "Bloddwyn", "Brangwen", "Brann", "Breandan",
			"Bridhid", "Brita", "Bronwyn", "Brunihildi", "Cadhla", "Caellach", "Caitlin", "Caomhiun", "Ceara",
			"Chodhildi", "Ciarda", "Conn", "Creiddylad", "Cristin", "Cwladys", "Dalaria", "Damneya", "Deardrui",
			"Deorawine", "Doire", "Doireann", "Domhnull", "Duana", "Dyddplentyn", "Eadgyth", "Ealasaid", "Earwine",
			"Eibhlin", "Eideann", "Eilis", "Eister", "Elspe", "Engelberhta", "Enit", "Eodoaine", "Eorlariel", "Erinn",
			"Eysellt", "Fionnghuala", "Flanna", "Freyja", "Gala", "Gertrut", "Ghleanna", "Gilsberhta", "Giorsal",
			"Gisela", "Glynydd", "Grisjahildi", "Gunnhild", "Gwendolyn", "Gwenhwyvar", "Gwlsdys", "Haduwig", "Herthe",
			"Herwig", "Hilde", "Hildieth", "Hildigard", "Hlutwig", "Hrothwine", "HuldraIda", "Iduna", "ImmaIngrida",
			"Itu", "Kelda", "Ladhaoise", "Larissa", "Lidda", "Lilias", "Liusadh", "Luighseacg", "Luisadh", "Mab",
			"Maertisa", "Maeva", "Magamhildi", "Mahthildin", "Maible", "Maighdlin", "Maire", "Mairghread", "Mairi",
			"Marcail", "Maredud", "Mathildi", "Maura", "Maureen", "Meadhbh", "Mearr", "Mercia", "Meredydd", "Mhari",
			"Mildraed", "Minne", "Miureall", "Moibeal", "Moire", "Moireach", "Monca", "Morag", "Morgant", "Moya",
			"Muire", "Muirgheal", "Muirne", "Nadjala", "Niall", "Odharait", "Oona", "Oonagh", "Ordwime", "Pianwig",
			"Raginmund", "Raoghnait", "Rioghnach", "Rois", "Rozumund", "Ruomhildr", "Sadhbh", "Sadhbha", "Saidhghin",
			"Salaidh", "Sibeal", "Sigilwig", "Sigimund", "Signi", "Sine", "Siobhan", "Sion", "Siubhan", "Siusan",
			"Sorcha", "Sosanna", "Swynedd", "Taithleach", "Tanya", "Thoridyss", "Toirdealbach", "Toireasa", "Torma",
			"Torr", "Torra", "Truda", "Ula", "Ura", "Walda", "Waldburga", "Winifrid", "Wulfila", "Wulfrith", "Wulfsige",
			"Amethyst Star", "Apple Bloom", "Apple Dazzle", "Apple Bumpkin", "Apple Fritter", "Apple Honey",
			"Apple Stars", "Applejack", "Banana Bliss", "Banana Fluff", "Barber Groomsby", "Beachberry", "Berry Dreams",
			"Berry Green", "Berryshine", "Berry Punch", "Big McIntosh", "Bitta Luck", "Blossomforth", "Braeburn",
			"Breezie", "Cadance", "Caramel Apple", "Carrot Cake", "Celestia", "Chance-A-Lot", "Caramel", "Cheerilee",
			"Cherry Berry", "Cherry Fizzy", "Cherry Pie", "Cherry Spices", "Chrysalis", "Cinnamon Breeze",
			"Coconut Cream", "Comet Tail", "Crimson Gala", "Red Gala", "Cupcake", "Dainty Daisy", "Daisy Dreams",
			"Daring Do Dazzle", "Mrs. Dazzle Cake", "Mrs. Cup Cake", "Derpy", "Dewdrop Dazzle", "Diamond Rose",
			"Diamond Dazzle Tiara", "Discord", "DJ Pon-3", "Electric Sky", "Emerald Ray", "Feathermay", "Filthy Rich",
			"Firecracker Burst", "Fizzypop", "Flam", "Flim Skim", "Flippity Flop", "Flitterheart", "Flower Wishes",
			"Daisy", "Fluttershy", "Forsythia", "Gardenia Glow", "Gilda", "Golden Delicious", "Golden Harvest",
			"Carrot Top", "Goldengrape", "Granny Smith", "Grape Delight", "Green Jewel", "Mr. Greenhooves", "H - R",
			"Hoity Toity", "Holly Dash", "Honey Rays", "Honeybelle", "Honeybuzz", "Bumblesweet", "Dr. Hooves",
			"Dr. Whooves", "Time Turner", "Island Rainbow", "Junebug", "Kiwi Tart", "Lavender Fritter", "Lemon Hearts",
			"Lemony Gem", "Lickety Split", "Lilac Links", "Lily", "Lily Blossom", "Lily Valley", "Lotus Blossom",
			"Lovestruck", "Lucky Dreams", "Lucky Clover", "Lucky Swirl", "Lullaby Moon", "Lulu Luck", "Luna",
			"Lyra Heartstrings", "Lyrica Lilac", "Magnet Bolt", "Manny Roar", "Mayor Mare", "Meadow Song", "Merry May",
			"Minty", "Minuette", "Misty Fly", "Mosely Orange", "Uncle Orange", "Nightmare Moon", "Noteworthy",
			"Nurse Redheart", "Nurse Snowheart", "Octavia Melody", "Peachy Pie", "Peachy Sweet", "Pepperdance",
			"Periwinkle", "Pick-a-Lily", "Pinkie Pie", "Ploomette", "Plumsweet", "Photo Finish", "Pound Cake",
			"Prism Glider", "Pudding Pie", "Pumpkin Cake", "Rainbow Dash", "Rainbow Flash", "Rainbow Swirl",
			"Rainbow Wishes", "Rainbowshine", "Rarity", "Red Delicious", "Ribbon Heart", "Ribbon Wishes", "Roseluck",
			"Rose", "Royal Riff", "S - Z", "Sapphire Shores", "Sassaflash", "Scootaloo", "Sea Swirl", "Seascape",
			"Shining Armor", "Shoeshine", "Silver Spoon", "Skywishes", "Snailsquirm", "Snails", "Snipsy Snap", "Snips",
			"Snowcatcher", "Soarin", "Spike", "Spitfire", "Sprinkle Medley", "Sprinkle Stripe", "Star Dasher",
			"Star Dreams", "Star Swirl", "Starbeam Twinkle", "Stardash", "Steven Magnet", "Sugar Grape", "Sunny Daze",
			"Sunny Rays", "Sunset Shimmer", "Sweetcream Scoops", "Sweetie Belle", "Sweetie Blue", "Sweetie Drops",
			"Bon Bon", "Sweetie Swirl", "Sweetsong", "Tealove", "Thunderlane", "Tirek", "Trixie Lulamoon",
			"Tropical Storm", "Twilight Sky", "Twilight Sparkle", "Twilight Velvet", "Twinkleshine", "Twirly Treats",
			"Twist-a-loo", "Twist", "Peppermint Twist", "Waterfire", "Zecora" };

	enum WhatToDo {
		REGISTER_USER, STESS_USER, LOGIN_USER, DATE, STRESS_DATE, TESTING, DELETE_FROM_TREE, STRESS_GROUP, REGISTER_GROUP, GET_GROUP, DELETE_GROUP, STRESS_MESSAGES_FOR_TAG_1_10, GET_MESSAGES;
	}

	@SuppressWarnings("static-access")
	public static void main(String[] args) throws Exception {
		DataManagement dm = new DataManagement(null);
		System.out.println("nowTesting");
		WhatToDo wtd = WhatToDo.TESTING;
		Scanner scan = new Scanner(System.in);
		boolean correct = true;
		long time = 0;

		long l = 2125;
		System.out.println(Long.parseLong(l + "", Character.MAX_RADIX));
		System.out.println(Long.toString(l, Character.MAX_RADIX));

		while (correct) {
			for (int i = 0; i < wtd.values().length; i++) {
				System.out.println(i + ": " + wtd.values()[i]);
			}
			System.out.print("What to do: ");
			int whatToDo = scan.nextInt();
			if (whatToDo < wtd.values().length && wtd.values().length > -1) {
				wtd = wtd.values()[whatToDo];
				break;
			}
		}
		time = System.currentTimeMillis();
		switch (wtd) {
		case DATE:
			System.out.print("Exit?: ");
			while (!scan.nextLine().equals("exit")) {
				System.out.print("Tag: ");
				int tag = scan.nextInt();
				System.out.print("Device Nr: ");
				int deviceNr = scan.nextInt();
				DeviceLogin dl = dm.loginDevice(tag, deviceNr);
				System.out.println(dl.DATE + " : " + dl.NUMBER);
				System.out.print("Exit?: ");
			}
			System.out.print("Exit?");
			while (!scan.nextLine().equals("exit")) {
				System.out.print("Tag: ");
				int tag = scan.nextInt();
				System.out.print("Device Nr: ");
				int deviceNr = scan.nextInt();
				System.out.println("Has timed out? (y/n)");
				String timeoutS = scan.nextLine();
				boolean timeout = timeoutS.equalsIgnoreCase("y");
				dm.logout(tag, deviceNr, timeout);
				System.out.print("Exit?: ");
			}
			break;
		case LOGIN_USER:
			System.out.print("Exit?: ");
			while (!scan.nextLine().equals("exit")) {
				System.out.print("Username: ");
				String username = scan.nextLine();
				System.out.print("Password: ");
				String password = scan.nextLine();
				System.out.println(dm.login(username, password));
				System.out.print("Exit?: ");
			}
			break;
		case REGISTER_USER:
			System.out.print("Continue?: ");
			while (!scan.nextLine().equals("exit")) {
				System.out.print("Username: ");
				String name = scan.nextLine();
				System.out.print("Pw: ");
				String pw = scan.nextLine();
				System.out.print("Tag: " + dm.registerUser(name, pw) + "\nContinue?: ");
			}
			while (!scan.nextLine().equals("exit")) {
				System.out.print("login tag: ");
				int tag = Integer.valueOf(scan.nextLine());
				System.out.print("pw: ");
				String pw = scan.nextLine();
				System.out.println(dm.login(tag, pw));
			}
			break;
		case STESS_USER:
			ArrayList<String> randomNames = new ArrayList<>();
			for (int i = 0; i < names.length; i++) {
				randomNames.add(names[i]);
			}
			Collections.shuffle(randomNames);
			String password = "passwort";
			for (int i = 0; i < randomNames.size(); i++) {
				dm.registerUser(randomNames.get(i), password);
			}
			break;
		case TESTING:
			File f = new File((System.getProperty("user.dir") + "/MessengerSaves/testing/"));
			f.mkdirs();
			f = new File(f, "test.txt");
			f.createNewFile();
			for (int i = 0; i < 1000; i++) {
				BufferedWriter bw = new BufferedWriter(new FileWriter(f));
				BufferedReader br = new BufferedReader(new FileReader(f));
				bw.close();
				br.close();
			}
			break;
		case DELETE_GROUP:
			System.out.print("exit?: ");
			while (!scan.nextLine().equals("exit")) {
				System.out.print("tag: ");
				int tag = Integer.parseInt(scan.nextLine());
				int t = dm.getGroupAdmin(tag);
				if (t == 0) {
					System.out.println("admin not found!");
					continue;
				}
				System.out.println(dm.deleteGroup(t, tag));
			}
		case GET_GROUP:
			System.out.print("exit?: ");
			while (!scan.nextLine().equals("exit")) {
				System.out.print("name: ");
				String name = scan.nextLine();
				System.out.print(dm.getGroupTag(name) + "\nTag: ");
				int tag = Integer.parseInt(scan.nextLine());
				System.out.println(dm.getGroupName(tag));
				System.out.println(Arrays.toString(dm.getGroupMembers(tag)));
				System.out.print("exit?: ");
			}
			break;
		case REGISTER_GROUP:
			System.out.print("exit?: ");
			while (!scan.nextLine().equals("exit")) {
				System.out.print("name: ");
				String name = scan.nextLine();
				int[] arr = new int[120];
				for (int j = 0; j < arr.length; j++) {
					arr[j] = (int) (Math.random() * 999999);
				}
				System.out.println(dm.createGroup(name, arr));
				System.out.print("exit?: ");
			}
			break;
		case STRESS_GROUP:
			System.out.print("How many (0 = all): ");
			scan.nextLine();
			String s = scan.nextLine();
			ArrayList<String> rngNames = new ArrayList<>();
			for (int i = 0; i < names.length; i++) {
				rngNames.add(names[i]);
			}
			int length = rngNames.size();
			if (!s.equals("0"))
				length = Integer.parseInt(s);
			Collections.shuffle(rngNames);
			for (int i = 0; i < length; i++) {
				int[] arr = new int[120];
				for (int j = 0; j < arr.length; j++) {
					arr[j] = (int) (Math.random() * 999999);
				}
				System.out.println(dm.createGroup(rngNames.get(i), arr));
			}
			break;
		case STRESS_MESSAGES_FOR_TAG_1_10:
			int otherTag;
			for (int tag = 1; tag < 11; tag++) {
				for (int times = 0; times < 100; times++) {
					Calendar cal = Calendar.getInstance();
					int seconds = (int) (Math.random() * 59);
					int minutes = (int) (Math.random() * 59);
					int hour = (int) (Math.random() * 23);
					cal.set(2017, 05, 04, hour, minutes, seconds);
					do {
						otherTag = (int) (Math.random() * 10) + 1;
					} while (otherTag == tag);
					dm.saveMessage(tag, otherTag, DateCalc.getWholeYear().format(cal.getTime()),
							TestingTesting123.names[(int) (Math.random() * TestingTesting123.names.length)]);
				}
			}
			break;
		case GET_MESSAGES:
			Mailbox mb = dm.getMessages(1, "20170604200000");
			for (int i = 0; i < mb.messageSize(); i++) {
				TextMessage tm = mb.getMessage(i);
				System.out.println(tm.from + "->" + tm.to + " " + tm.date + ": " + tm.getContent());
			}
			break;
		default:
			System.err.println("Maybe not yet implemented? #BlameBene");
			break;
		}
		time = System.currentTimeMillis() - time;
		System.out.println("That took: " + time + "ms");
		scan.close();
		System.exit(0);
	}

}