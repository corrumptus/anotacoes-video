"use client";

import { useRouter } from "next/navigation";
import useAuthenticationRedirection, { WhenRedirect } from "@hooks/useAuthenticationRedirection";

export default function Home() {
  const router = useRouter();

  useAuthenticationRedirection(WhenRedirect.AUTHENTICATED);

  return (
    <div className="h-full flex flex-col">
      <header className="flex items-center justify-between px-[5%] py-2.5 bg-orange-400">
        <div className="flex items-center gap-1">
          <img
            src="https://cdn-icons-png.flaticon.com/512/8632/8632957.png"
            alt="logo do site anotações-vídeo(uma caderneta com uma caneta)"
            className="w-[35px]"
          />
          <h1 className="text-2xl">Avídeotação</h1>
        </div>
        <div className="flex gap-2.5">
          <button
            className="font-bold outline-none cursor-pointer hover:text-shadow-white hover:text-shadow-md"
            onClick={() => router.push("/login")}
          >
            Entre
          </button>
          <button
            className="font-bold border-2 border-black rounded-2xl px-2 py-0.5 outline-none cursor-pointer hover:shadow-white hover:shadow-sm"
            onClick={() => router.push("/signup")}
          >
            Inscreva-se
          </button>
        </div>
      </header>
      <main className="h-full bg-[#ffffbd] flex items-center justify-center">
        <div className="h-[90%] w-[90%] bg-lime-400 flex flex-col items-center justify-center gap-4 rounded-2xl">
          <div>
            <h1 className="font-bold text-white text-2xl border-b border-red-600">Para todo vídeo, uma anotação</h1>
            <p className="text-white text-xl border-b border-red-600">Anote hoje, relembre amanhã</p>
            <div className="border-b border-red-600">
              <button
                className="bg-orange-400 rounded-2xl font-bold my-1 px-1 py-0.5 outline-none cursor-pointer hover:shadow-white hover:shadow-sm"
                onClick={() => router.push("/signup")}
              >
                Começe agora
              </button>
            </div>
          </div>
        </div>
      </main>
    </div>
  );
}
