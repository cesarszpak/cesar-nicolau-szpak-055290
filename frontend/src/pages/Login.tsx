import React from 'react'

const Login: React.FC = () => {
  return (
    <div className="bg-login">
      <div className="card-login">
        <h1 className="title-login">Bem vindo a página de login!</h1>

        <div className="form-group-login">
          <label className="form-label-login">Email</label>
          <input className="form-input-login" type="email" placeholder="seu@email.com" />
        </div>

        <div className="form-group-login">
          <label className="form-label-login">Senha</label>
          <input className="form-input-login" type="password" placeholder="••••••••" />
        </div>

        <div className="btn-group-login">
          <a className="link-login" href="#/forgot">Esqueceu a senha?</a>
          <button className="btn-primary">Entrar</button>
        </div>
      </div>
    </div>
  )
}

export default Login
