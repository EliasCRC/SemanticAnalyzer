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

	public void fillFeatures() {
		for (AbstractSymbol classKey : classMap.keySet()) {
			ClassNode currClass = classMap.get(classKey);
			currClass.fillFeatures();
		}
	}

	public void traverse() {
		for (AbstractSymbol classKey : classMap.keySet()) {
			ClassNode currClass = classMap.get(classKey);
			currClass.traverse(this);
		}
	}
}

class ClassNode {
	public AbstractSymbol parentName;
	public AbstractSymbol fileName;
	public AbstractSymbol className;
	public class_c errorClass;

	public HashMap<AbstractSymbol, AttributeNode> attributeMap;
	public HashMap<AbstractSymbol, MethodNode> methodMap;
	
	public ClassNode(AbstractSymbol fileName, AbstractSymbol className, AbstractSymbol parentName, class_c errorClass) {
		this.fileName = fileName;
		this.parentName = parentName;
		this.errorClass = errorClass;
		this.className = className;

		attributeMap = new HashMap<AbstractSymbol, AttributeNode>();
		methodMap = new HashMap<AbstractSymbol, MethodNode>();
	}

	public void fillFeatures() {
		for (AbstractSymbol methodKey : methodMap.keySet()) {
			MethodNode currMethod = methodMap.get(methodKey);
			currMethod.fillParents(className);
		}
		for (AbstractSymbol attrKey : attributeMap.keySet()) {
			AttributeNode currAttr = attributeMap.get(attrKey);
			currAttr.fillParents(className);
		}
	}

	public void traverse(ProgramTable progTable) {
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
	public AbstractSymbol fatherClass;
	public AbstractSymbol name;
	public AbstractSymbol type;
	public ExpressionNode init;

	public AttributeNode(AbstractSymbol type, AbstractSymbol name, Expression init) {
		this.name = name;
		this.type = type;
		this.init = new ExpressionNode(init);
	}

	public void fillParents(AbstractSymbol className) {
		this.fatherClass = className;
		init.fillParents(className, name, true);
	}

	public void traverse(ProgramTable progTable) {
		init.traverse(progTable);
	}
}

class MethodNode {
	public AbstractSymbol fatherClass;
	public AbstractSymbol name;
	public AbstractSymbol returnType;
	public ExpressionNode expr;
	public HashMap<AbstractSymbol, Expression> scope;

	public HashMap<AbstractSymbol, FormalNode> formalMap;

	public MethodNode(AbstractSymbol returnType, AbstractSymbol name, Expression expr) {
		formalMap = new HashMap<AbstractSymbol, FormalNode>();
		this.expr = new ExpressionNode (expr);
		this.name = name;
		this.returnType = returnType;
		scope = new HashMap<AbstractSymbol, Expression>();
	}

	public void fillParents(AbstractSymbol className) {
		this.fatherClass = className;
		expr.fillParents(className, name, false);
	}
	
	public void traverse(ProgramTable progTable) {
		expr.traverse(progTable);
	}
}

class FormalNode {
	public AbstractSymbol type;

	public FormalNode(AbstractSymbol type) {
		this.type = type;
	}
}

class ExpressionNode {
	public AbstractSymbol className;
	public AbstractSymbol methodName;
	public AbstractSymbol type;
	public Expression expr;
	public boolean isInit;

	public ExpressionNode (Expression expr) {
		this.expr = expr;
		this.type = expr.get_type();
	}

	public void fillParents(AbstractSymbol className, AbstractSymbol methodName, boolean isInit) {
		this.className = className;
		this.methodName = methodName;
		this.isInit = isInit;
	}

	public void traverse(ProgramTable progTable) {
		expr.analyze(this, progTable);
	}
}
