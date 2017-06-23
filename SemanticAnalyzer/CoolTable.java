import java.util.HashMap;

/*
* Archivos Útiles:
* See SymbolTable.java
* See TreeConstants.java
*/

class ProgramTable {

	public HashMap<AbstractSymbol, ClassNode> classMap; //Mapa que relaciona Nombre -> Clase
	public ClassTable classTable;		//Para reporte de errores

	//-----------------------------------------------------------------------------------------
	// Constructor
	//-----------------------------------------------------------------------------------------
	public ProgramTable(ClassTable classTable) {
		this.classMap = new HashMap<AbstractSymbol, ClassNode>();
		this.classTable = classTable;
	}

	//-----------------------------------------------------------------------------------------
	// Imprime la tabla para verificar que se construyó correctamente
	//-----------------------------------------------------------------------------------------
	public void print() {
		for (AbstractSymbol classKey : classMap.keySet()) {
			ClassNode currClass = classMap.get(classKey);
			System.out.println("Class: " + classKey + ", Parent: " + currClass.parentName);
			for (AbstractSymbol attributeKey : currClass.attributeMap.keySet()) {
				AttributeNode currAttr = currClass.attributeMap.get(attributeKey);
				System.out.println("\tAttr " + currAttr.name + " = Type: " + currAttr.type + " Expr Type: " 
						+ currAttr.init.type + " Parent Class = " + currAttr.fatherClass);
			}
			for (AbstractSymbol methodKey : currClass.methodMap.keySet()) {
				MethodNode currMethod = currClass.methodMap.get(methodKey);
				System.out.println("\tMethod " + currMethod.name + " = Return Type: " + currMethod.returnType 
						+ " Expr Type: " + currMethod.expr.type
						+" Parent Class = " + currMethod.fatherClass);
				System.out.print("\t\tFormal types: (");
				for (AbstractSymbol formalKey : currMethod.formalMap.keySet()) {
					FormalNode currFormal = currMethod.formalMap.get(formalKey);
					System.out.print(currFormal.type + ", ");
				}
				System.out.println(")");
				System.out.println("\t\tExpr = ParentClass: " + currMethod.expr.className 
						+ " MethodName: " + currMethod.expr.methodName
						+ " Type: " + currMethod.expr.type
						+ " IsInit: " + currMethod.expr.isInit);
			}
		}
	}

	//-----------------------------------------------------------------------------------------
	// Le dice a las clases que le indiquen a los features cual es su clase contenedora
	//-----------------------------------------------------------------------------------------
	public void fillFeatures() {
		for (AbstractSymbol classKey : classMap.keySet()) {
			ClassNode currClass = classMap.get(classKey);
			currClass.fillFeatures();
		}
	}
	
	//-----------------------------------------------------------------------------------------
	// Recorre la tabla para la verificación semántica, y se manda a sí mismo
	// Por si necesitan recorrer la tabla por separado.
	//-----------------------------------------------------------------------------------------
	public void traverse() {

		for (AbstractSymbol classKey : classMap.keySet()) {
			ClassNode currClass = classMap.get(classKey);
			currClass.traverse(this);
		}
	}
}

class ClassNode {
	public AbstractSymbol parentName; 	//Nombre de clase de la que hereda
	public AbstractSymbol fileName;		//Nombre de archivo
	public AbstractSymbol className;	//Nombre de ella misma 
	public class_c errorClass;		//Nodo del arbol para erores

	
	public SymbolTable symbolTable;  //scope de la clase
	public HashMap<AbstractSymbol, AttributeNode> attributeMap;	//Mapa para relacionar Nombre -> Atributo
	public HashMap<AbstractSymbol, MethodNode> methodMap;		//Mapa para relacionar Nombre -> Método
	
	//-----------------------------------------------------------------------------------------
	// Constructor
	//-----------------------------------------------------------------------------------------
	public ClassNode(AbstractSymbol fileName, AbstractSymbol className, AbstractSymbol parentName, class_c errorClass) {
		this.fileName = fileName;
		this.parentName = parentName;
		this.errorClass = errorClass;
		this.className = className;

		symbolTable = new SymbolTable();  // se inicializa el scope de la clase
		symbolTable.enterScope();
		attributeMap = new HashMap<AbstractSymbol, AttributeNode>();
		methodMap = new HashMap<AbstractSymbol, MethodNode>();
	}

	//-----------------------------------------------------------------------------------------
	// Le dice a los Features cual es su clase contenedora
	//-----------------------------------------------------------------------------------------
	public void fillFeatures() {
		for (AbstractSymbol methodKey : methodMap.keySet()) {
			MethodNode currMethod = methodMap.get(methodKey);
			currMethod.fillParents(className);
		}
		for (AbstractSymbol attrKey : attributeMap.keySet()) {
			AttributeNode currAttr = attributeMap.get(attrKey);
			currAttr.fillParents(className);
			symbolTable.addId(attrKey, currAttr.type);
		}
	}

	//-----------------------------------------------------------------------------------------
	// Recorre los métodos y atributos, además hace la respectiva revisión semántica
	//-----------------------------------------------------------------------------------------
	public void traverse(ProgramTable progTable) {
		ClassTable errorReport = progTable.classTable;
		/* Un error se reportaría de la manera:
 
			errorReport.semantError(this.errorClass);
			System.out.println("El error");
		*/

		for (AbstractSymbol methodKey : methodMap.keySet()) {
			MethodNode currMethod = methodMap.get(methodKey);
			currMethod.traverse(progTable);
		}
		for (AbstractSymbol attrKey : attributeMap.keySet()) {
			AttributeNode currAttr = attributeMap.get(attrKey);
			currAttr.traverse(progTable);
		}
	}

}

class AttributeNode {
	public AbstractSymbol fatherClass;	//Nombre de la clase que la contiene
	public AbstractSymbol name;		//Nombre del atributo
	public AbstractSymbol type;		//Tipo específicado por usuario
	public ExpressionNode init;		//Expresion de inicializacion
	public attr errorAttribute;		//Nodo del árbol para reportar errores

	//-----------------------------------------------------------------------------------------
	// Constructor
	//-----------------------------------------------------------------------------------------
	public AttributeNode(AbstractSymbol type, AbstractSymbol name, attr errorAttribute, Expression init) {
		this.name = name;
		this.type = type;
		this.errorAttribute = errorAttribute;
		this.init = new ExpressionNode(init);
	}

	//-----------------------------------------------------------------------------------------
	// Pone el nombre de la clase padre y manda valores necesarios para que la expresion
	// esté completa.
	//-----------------------------------------------------------------------------------------
	public void fillParents(AbstractSymbol className) {
		this.fatherClass = className;
		init.fillParents(className, name, true);
	}

	//-----------------------------------------------------------------------------------------
	// Manda a revisar la inicialización, además hace la respectiva revisión semántica
	//-----------------------------------------------------------------------------------------
	public void traverse(ProgramTable progTable) {
		ClassTable errorReport = progTable.classTable;
		AbstractSymbol fileErrorName = ( progTable.classMap.get(this.fatherClass) ).fileName;
		/* Un error se reportaría de la manera:
 
			errorReport.semantError(fileErrorName, errorAttribute);
			System.out.println("El error");
		*/

		init.traverse(progTable);

	}
}

class MethodNode {
	public AbstractSymbol fatherClass;	//Nombre de la clase que la contiene
	public AbstractSymbol name;		//Nombre del método
	public AbstractSymbol returnType;	//Tipo de retorno especificado
	public ExpressionNode expr;		//Expresion que contiene el método
	public method errorMethod;		//Nodo del árbol para reportar errores

	public HashMap<AbstractSymbol, FormalNode> formalMap = new HashMap<AbstractSymbol, FormalNode>();
	//Relaciona con un mapa los parámetros de forma Nombre -> Formal

	//-----------------------------------------------------------------------------------------
	// Constructor
	//-----------------------------------------------------------------------------------------
	public MethodNode(AbstractSymbol returnType, AbstractSymbol name, method errorMethod, Expression expr) {
		this.name = name;
		this.returnType = returnType;
		this.errorMethod = errorMethod;
		this.expr = new ExpressionNode (expr);

		formalMap = new HashMap<AbstractSymbol, FormalNode>();
	}

	//-----------------------------------------------------------------------------------------
	// Pone el nombre de la clase padre y manda valores necesarios para que la expresion
	// esté completa.
	//-----------------------------------------------------------------------------------------
	public void fillParents(AbstractSymbol className) {
		this.fatherClass = className;
		expr.fillParents(className, name, false);
	}

	//-----------------------------------------------------------------------------------------
	// Manda a revisar la expresión, además hace la respectiva revisión semántica
	//-----------------------------------------------------------------------------------------
	public void traverse(ProgramTable progTable) {
		ClassTable errorReport = progTable.classTable;
		AbstractSymbol fileErrorName = ( progTable.classMap.get(fatherClass) ).fileName;
		/* Un error se reportaría de la manera:
 
			errorReport.semantError(fileErrorName, errorMethod);
			System.out.println("El error");
		*/
		progTable.classMap.get(fatherClass).symbolTable.enterScope();  // entra a un nuevo scope (el del metodo)
		expr.traverse(progTable);
		progTable.classMap.get(fatherClass).symbolTable.exitScope();  // sale de ese scope (el del metodo)
	}
}

class FormalNode {
	public AbstractSymbol type; //Tipo del parámetro

	//-----------------------------------------------------------------------------------------
	// Constructor
	//-----------------------------------------------------------------------------------------
	public FormalNode(AbstractSymbol type) {
		this.type = type;
	}
}

class ExpressionNode {
	public AbstractSymbol className;	//Nombre de la clase en que está
	public AbstractSymbol methodName;	//Nombre del método en que está
	public AbstractSymbol type;		//Tipo de la expresión init (atributo) o expr (método)
	public Expression expr;			//Expresión init o expr
	public boolean isInit;			//Indica si es una inicialización de atributo o expresión de método

	//-----------------------------------------------------------------------------------------
	// Constructor
	//-----------------------------------------------------------------------------------------
	public ExpressionNode (Expression expr) {
		this.expr = expr;
		this.type = expr.get_type();
	}

	//-----------------------------------------------------------------------------------------
	// Llena todos los campos de la expresión que no se llenaron en el constructor
	//-----------------------------------------------------------------------------------------
	public void fillParents(AbstractSymbol className, AbstractSymbol methodName, boolean isInit) {
		this.className = className;
		this.methodName = methodName;
		this.isInit = isInit;
	}

	//-----------------------------------------------------------------------------------------
	// Manda a revisar la o las expresiones que contenga es expresión externa, 
	// además hace la respectiva revisión semántica
	//-----------------------------------------------------------------------------------------
	public void traverse(ProgramTable progTable) {

		expr.analyze(this, progTable);
	}
}
