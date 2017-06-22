import java.util.HashMap;
class ProgramTable {
	public HashMap<AbstractSymbol, ClassNode> classMap;
	public ClassTable classTable;

	public ProgramTable(ClassTable classTable) {
		this.classMap = new HashMap<AbstractSymbol, ClassNode>();
		this.classTable = classTable;
	}

	public void print() {
		for (AbstractSymbol classKey : classMap.keySet()) {
			ClassNode currClass = classMap.get(classKey);
			System.out.println("Class: " + classKey + ", Parent: " + currClass.parentName);
			for (AbstractSymbol attributeKey : currClass.attributeMap.keySet()) {
				AttributeNode currAttr = currClass.attributeMap.get(attributeKey);
				System.out.println("\tAttr = Type: " + currAttr.type + " Expr Type: " + currAttr.init.get_type());
			}
			for (AbstractSymbol methodKey : currClass.methodMap.keySet()) {
				MethodNode currMethod = currClass.methodMap.get(methodKey);
				System.out.println("\tMethod = Return Type: " + currMethod.returnType 
						+ " Expr Type: " + currMethod.expr.get_type());
				System.out.print("\t\tFormal types: (");
				for (AbstractSymbol formalKey : currMethod.formalMap.keySet()) {
					FormalNode currFormal = currMethod.formalMap.get(formalKey);
					System.out.print(currFormal.type + ", ");
				}
				System.out.println(")");
			}
		}
	}
}

class ClassNode {
	public AbstractSymbol parentName;
	public AbstractSymbol fileName;
	public HashMap<AbstractSymbol, AttributeNode> attributeMap;
	public HashMap<AbstractSymbol, MethodNode> methodMap;
	
	public ClassNode(AbstractSymbol fileName, AbstractSymbol parentName) {
		this.fileName = fileName;
		this.parentName = parentName;
		attributeMap = new HashMap<AbstractSymbol, AttributeNode>();
		methodMap = new HashMap<AbstractSymbol, MethodNode>();
	}
}

class AttributeNode {
	public AbstractSymbol type;
	public Expression init;

	public AttributeNode(AbstractSymbol type, Expression init) {
		this.type = type;
		this.init = init;
	}
}

class MethodNode {
	public AbstractSymbol returnType;
	public HashMap<AbstractSymbol, FormalNode> formalMap = new HashMap<AbstractSymbol, FormalNode>();
	public Expression expr;

	public MethodNode(AbstractSymbol returnType, Expression expr) {
		formalMap = new HashMap<AbstractSymbol, FormalNode>();
		this.expr = expr;
		this.returnType = returnType;
	}
}

class FormalNode {
	public AbstractSymbol type;

	public FormalNode(AbstractSymbol type) {
		this.type = type;
	}
}
