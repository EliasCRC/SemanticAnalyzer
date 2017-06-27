class C {
	x : Int;
	w : Bool;

	init() : C {
           {
		-- Si las dos ramas de un if son de tipo bool el if sera booleano tambien
		-- Esto nos permite anidar ifs como en el siguiente ejemplo:
		if if true then w <- false else w <- true fi 
		then x <- 2 else x <- 3 fi;
		self;
           }
	};
};

Class Main {
	main() : Bool {
		true
	};
};
