class C {
	x : Int;
	w : Bool;

	init() : C {
		{
		-- El predicado de un if tiene que ser booleano
		-- Por lo tanto fallara con un entero:
		if x <- 1
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
