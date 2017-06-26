class C {
	x : Int;
	w : Bool;

	init(y : Int) : C {
           {
		x <- 99; -- x esta en el scope de la clase
		let v : Bool, p : Int in {
		p <- 1;  -- p esta en el scope del let
		y <- p;  -- y esta en el scope del metodo (parametro)
		w <- true; -- w esta en el scope de la clase
		v <- false; -- v esta en el scope del let
		self;
		};
           }
	};
};

Class Main {
	main() : Bool {
		true
	};
};
