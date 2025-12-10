from flask import Flask, request, jsonify
import mysql.connector
from flask_cors import CORS

app = Flask(__name__)
CORS(app)  # permite o Android acessar

# Conexão com MySQL (altere para o servidor que você usar)
db = mysql.connector.connect(
    host="SEU_HOST",
    user="SEU_USUARIO",
    password="SUA_SENHA",
    database="SUA_DATABASE"
)

# --------------------------
# 1) Registrar encomenda
# --------------------------
@app.post("/registrar")
def registrar():
    data = request.json
    nome = data.get("nome")
    d
    descricao = data.get("descricao")

    if not nome or not descricao:
        return jsonify({"erro": "Campos vazios"}), 400

    cursor = db.cursor()
    cursor.execute("""
        INSERT INTO encomenda (nome, descricao, retirada)
        VALUES (%s, %s, 0)
    """, (nome, descricao))
    db.commit()

    return jsonify({"status": "ok"}), 201


# --------------------------
# 2) Listar encomendas NÃO RETIRADAS
# --------------------------
@app.get("/listar")
def listar():
    cursor = db.cursor(dictionary=True)
    cursor.execute("SELECT id, nome, descricao FROM encomenda WHERE retirada = 0")
    dados = cursor.fetchall()
    return jsonify(dados)


# --------------------------
# 3) Marcar encomenda como retirada
# --------------------------
@app.post("/retirar")
def retirar():
    data = request.json
    id = data.get("id")

    cursor = db.cursor()
    cursor.execute("UPDATE encomenda SET retirada = 1 WHERE id = %s", (id,))
    db.commit()

    return jsonify({"status": "retirado"})


app.run(host="0.0.0.0", port=5000)
