import Link from "next/link";
import { ChangeEvent, useState } from "react";
import useAuthentication, { AuthenticationInfos, AuthenticationType } from "@hooks/useAuthenticate";

export default function LoginForm() {
    const authentication = useAuthentication(AuthenticationType.LOGIN);
    const [ login, setLogin ] = useState<AuthenticationInfos<AuthenticationType.LOGIN>>({ login: "", password: "" });
    const [ isClickable, setIsClickable ] = useState(true);

    function updateLogin(e: ChangeEvent<HTMLInputElement>) {
        setLogin(cur => ({...cur, [e.target.id]: e.target.value}));
    }

    async function sendLogin() {
        setIsClickable(false);

        await authentication(login);

        setIsClickable(true);
    }

    return (
        <form
            className="bg-[#ffffbd] rounded-2xl p-4 flex flex-col gap-2"
            onSubmit={e => !(e.preventDefault() as undefined) && isClickable && sendLogin()}
        >
            <div className="border-b border-red-600">
                <h1 className="text-3xl font-bold">Entrar</h1>
            </div>
            <div className="border-b border-red-600">
                <label htmlFor="login" className="text-xl mr-1 cursor-pointer">Usuário:</label>
                <input
                    id="login"
                    type="text"
                    className="outline-none"
                    value={login.login}
                    onChange={updateLogin}
                />
            </div>
            <div className="border-b border-red-600">
                <label htmlFor="password" className="text-xl mr-1 cursor-pointer">Senha:</label>
                <input
                    id="password"
                    type="password"
                    className="outline-none"
                    value={login.password}
                    onChange={updateLogin}
                />
            </div>
            <div className="border-b border-red-600">
                <button
                    className={`${isClickable ? "bg-orange-400" : "bg-gray-500"} rounded-2xl px-2 py-0.5 outline-none cursor-pointer mb-1`}
                    onClick={() => isClickable && sendLogin()}
                >
                    Entrar
                </button>
            </div>
            <div className="border-b border-red-600">
                Não tem uma conta? <Link href="/login" className="text-blue-900">Cadastre-se</Link>
            </div>
        </form>
    )
}
