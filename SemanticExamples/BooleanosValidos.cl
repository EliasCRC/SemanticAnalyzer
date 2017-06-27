class C {
	x : Int;
	w : Bool <- true;

	init() : Bool {
           let z : Bool <- not w in {
		if 1 < 2 then w <- false else w <- true fi;

		if 1 = 1 then w <- false else w <- true fi;

		if w = w  then w <- false else w <- true fi;

		if "a" = "a"  then w <- false else w <- true fi;

   		if w then w <- false else w <- true fi;

		w <- 1 <= 2; 
		}
	};
};

Class Main {
	main() : Bool {
		true
	};
};
