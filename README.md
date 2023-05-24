# Lexer

Lexer (Лексер) - необходимая часть для работы парсера, которая выполняет первичную обработку исходного кода в лист с токенами. Находится в пакете `axl.lexer`.

```java
public class Test;

private int x;

public void main()
{
    int y = 10;
    #x = 54 + y;
    Console.println(sqrt(#x));
}
```

будет обработан в

```py
{
RWORD: public,
RWORD: class,
WORD: "Test",
SEMI,
RWORD: private,
RWORD: int,
WORD: "x",
SEMI,
RWORD: public,
RWORD: void,
WORD: "main"
LPAR,
RPAR,
LBRACE,
RWORD: int,
WORD: "y",
EQUAL,
INT: 10,
SEMI,
WORD: "this",
DOT,
WORD: "x",
EQUAL,
INT: 54,
PLUS,
WORD: "y",
SEMI,
WORD: "Console",
DOT,
WORD: "println",
LPAR,
WORD: "sqrt",
LPAR,
WORD: "this",
DOT,
WORD: "x",
RPAR,
RPAR,
SEMI,
RBRACE,
ENDFILE
}
```

Список всех токенов на момент версии `0.1-ALPHA`
```py
RWORD {
  public,
  private,
  protected,
  class,
  this,
  extend,
  implements,
  byte,
  char,
  short,
  int,
  long,
  float,
  double,
  void,
  if,
  else,
  switch,
  case,
  while,
  for,
  try,
  catch,
  finaly,
  throws,
  throw
},
EQUAL,                  # = (присваивание значения переменной)
PLUS,                   # + (сложение)
MINUS,                  # - (вычитание)
UNARY_MINUS,            # - (унарный минус)
DPLUS,                  # ++ (инкремент)
DMINUS,                 # -- (декремент)
MULTIPLY,               # * (умножение)
DIVIDE,                 # # / (деление)
MODULO,                 # % (остаток от деления)
GREATER_THAN,           # > (больше чем)
LESS_THAN,              # < (меньше чем)
GREATER_THAN_OR_EQUAL,  # >= (больше или равно)
LESS_THAN_OR_EQUAL,     # <= (меньше или равно)
EQUAL_TO,               # == (равно)
NOT_EQUAL_TO,           # != (не равно)
AND,                    # && (логическое И)
OR,                     # || (логическое ИЛИ)
NOT,                    # ! (логическое НЕ)
BITWISE_AND,            # & (побитовое И)
BITWISE_OR,             # | (побитовое ИЛИ)
BITWISE_XOR,            # ^ (побитовое исключающее ИЛИ)
BITWISE_NOT,            # ~ (побитовое НЕ)
LEFT_SHIFT,             # << (побитовый сдвиг влево)
RIGHT_SHIFT,            # >> (побитовый сдвиг вправо)
TERNARY1,               # ? (тернарный оператор)
TERNARY2,               # : (тернарный оператор)
LPAR,                   # (
RPAR,                   # )
SPEC_LPAR,              # ( (поставлен лексером)
SPEC_RPAR,              # ) (поставлен лексером)
LBRACE,                 # {
RBRACE,                 # }
LBRACKET,               # [
RBRACKET,               # ]
SEMI,                   # ;
DOT,                    # .
COMMA,                  # ,
INT,                    # (хранит число)
LONG,                   # (хранит число)
FLOAT,                  # (хранит число)
DOUBLE,                 # (хранит число)
CHAR,                   # (хранит число)
STRING,                 # (хранит строковую константу)
ENDFILE                 # (обозначение конца файла)
```

Лексер обрабатывет такие структуры как `+=`, `-=`, `*=`, `/=`, `>>=` и прочие в подобный вид
```java
x += 2-x;
```
```py
WORD: "x",
EQUAL,
WORD: "x",
PLUS,
SPEC_LPAR,
INT: 2,
MINUS,
WORD: "x",
SPEC_RPAR,
SEMI
```

А также обрабатывает символ `#` в виде `this.`
```java
#x = 2;
```
```py
WORD: "this",
DOT,
WORD: "x",
EQUAL,
INT: 2,
SEMI
```

И игнорирует комментарии `//`, `/* */`
```java
/*
  multiline
  комментарий
*/
x = 2; // singleline
```
```py
WORD: "x",
EQUAL,
INT: 2,
SEMI
```

Для взаимодействия лексер имеет подобную сигнатуру, где `Token` - класс для хранения типа и значения токена
```java
interface Lexer {
    public abstract List<Token> process(String);
}
```
