class C {
	intUno : Int;
	intDos : Int <- true;
	bool : Bool;
	string : String;
	
	init(x : Int, y : Bool) : Int {
           {
		intUno <- string;
		let intLocal : Int <- bool + intDos in (
			intDos <- (y * x) / string
		); 
		bool;
           }
	};
};

Class Main {
	intMain : Int <- 5;

	main():Int {
		intMain <- intMain * 2
	};
};
