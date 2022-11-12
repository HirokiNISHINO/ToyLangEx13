# ToyLangEx13
Toy Programming Language: Ex13

ローカル変数への代入ができるようにサポート。

現状の文法．

\<Program\> ::= \<Statements\>

\<Statements\> ::= [\<Statement\>]\*

\<Statement\> :: = \<ExprStmt>　| \<EmptyStmt\> | \<GlobalVarDec\> | \<LocalVarDec\>

\<GlobalVarDec\> ::= 'global' \<Type\> Identifier ';'

\<LocalVarDec\> ::= 'local' \<Type\> Identifier ';'

\<Type\> ::= 'int'

\<ExprStmt> ::= \<AdditiExpr\>　 ';'

\<EmptyStmt\> :: = ';'

\<AdditiExpr\>　:: = \<MultiplicativeExpr\> [ ( '+' | '-' ) \<MultiplicativeExpr\> ]\*

\<MultiplicativeExpr\> :: = \<Primary\> [ ( '\*'  | '/' ) \<Primary\> ]\*

\<Primary\> :: = ( \<AdditiExpr\> ) | \<Integer\>　| \<IdentifierOrAssignment\>　

\<IdentifierOrAssignment\>　 ::= \<Identifier\>　['=' \<AdditiExpr\>]
