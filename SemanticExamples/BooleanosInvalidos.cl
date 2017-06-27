class C {
	x : Int;
	w : Bool;

	init() : C {
           {

		if 1 < w then w <- false else w <- true fi;

		if 1 = "a" then w <- false else w <- true fi;

		if w = "a"  then w <- false else w <- true fi;

		if "a" = 1  then w <- false else w <- true fi;

    if "a" <= w  then w <- false else w <- true fi;

    if 3 then w <- false else w <- true fi;
		self;
           }
	};
};

Class Main {
	main() : Bool {
		true
	};
};
