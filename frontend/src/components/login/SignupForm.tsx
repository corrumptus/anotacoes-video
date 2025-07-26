import Link from "next/link";
import { ChangeEvent, useState } from "react";
import useAuthentication, { AuthenticationInfos, AuthenticationType } from "@hooks/useAuthenticate";

export default function SignupForm() {
    const authentication = useAuthentication(AuthenticationType.SIGNUP);
    const [ signup, setSignup ] = useState<AuthenticationInfos<AuthenticationType.SIGNUP>>({ login: "", password: "", profilePic: undefined });
    const [ confirmPassword, setConfirmPassword ] = useState("");
    const [ isClickable, setIsClickable ] = useState(true);

    function updateSignup(e: ChangeEvent<HTMLInputElement>) {
        setSignup(cur => ({
            ...cur,
            [e.target.id]: e.target.type === "file" ?
                (e.target.files || [])[0]
                :
                e.target.value
        }));
    }

    async function sendSignup() {
        if (confirmPassword !== signup.password) {
            alert("senhas diferentes");
            return;
        }

        setIsClickable(false);

        await authentication(signup);

        setIsClickable(true);
    }

    return (
        <form
            className="bg-[#ffffbd] rounded-2xl p-4 flex flex-col gap-2"
            onSubmit={e => !(e.preventDefault() as undefined) && isClickable && sendSignup()}
        >
            <div className="border-b border-red-600">
                <h1 className="text-3xl font-bold">Cadastrar</h1>
            </div>
            <div className="border-b border-red-600">
                <label htmlFor="login" className="text-xl mr-1 cursor-pointer">Usuário:</label>
                <input
                    id="login"
                    type="text"
                    className="outline-none"
                    value={signup.login}
                    onChange={updateSignup}
                />
            </div>
            <div className="border-b border-red-600">
                <label htmlFor="password" className="text-xl mr-1 cursor-pointer">Senha:</label>
                <input
                    id="password"
                    type="password"
                    className="outline-none"
                    value={signup.password}
                    onChange={updateSignup}
                />
            </div>
            <div className="border-b border-red-600">
                <label htmlFor="c-password" className="text-xl mr-1 cursor-pointer">Confirmar Senha:</label>
                <input
                    id="c-password"
                    type="password"
                    className="outline-none"
                    value={confirmPassword}
                    onChange={e => setConfirmPassword(e.target.value)}
                />
            </div>
            <div className="border-b border-red-600">
                <label htmlFor="profilePic" className="text-xl mr-1 cursor-pointer">Imagem de perfil:</label>
                <input
                    id="profilePic"
                    type="file"
                    className="outline-none cursor-pointer"
                    onChange={updateSignup}
                />
            </div>
            <div className="border-b border-red-600">
                <button
                    className={`${isClickable ? "bg-orange-400" : "bg-gray-500"} rounded-2xl px-2 py-0.5 outline-none cursor-pointer mb-1`}
                    onClick={() => isClickable && sendSignup()}
                >
                    Cadastrar
                </button>
            </div>
            <div className="border-b border-red-600">
                Já tem uma conta? <Link href="/login" className="text-blue-900">Entre</Link>
            </div>
        </form>
    )
}
