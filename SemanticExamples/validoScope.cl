class C {
	x : Int;
	w : Bool;

	init() : C {
           {
		x <- 99; -- x esta en el scope de la clase
		let p : Int <- 2, w : Bool in {
		p <- 1;  -- p esta en el scope del let
		w <- true; -- w esta en el scope de la clase
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
