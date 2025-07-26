"use client";

import { useEffect, useState } from "react";
import LoginForm from "@components/login/LoginForm";
import SignupForm from "@components/login/SignupForm";
import Argola from "@components/utils/Argola";
import { AuthenticationType } from "@hooks/useAuthenticate";

export default function FormResgistration({
    type
}: {
    type: AuthenticationType
}) {
    const [ argolas, setArgolas ] = useState<undefined[]>([]);

    useEffect(() => {
        function onResize() {
            setArgolas(new Array(Math.floor(document.querySelector("#signup")!.clientWidth/26)).fill(undefined));
        }

        window.addEventListener("resize", onResize);

        onResize();

        return () => {
            window.removeEventListener("resize", onResize);
        }
    }, []);

    return (
        <>
            <main className="h-full bg-lime-400 flex flex-col items-center justify-center">
                <div className="z-0 relative" id="signup">
                    {argolas
                        .map((_, i) =>
                            <Argola
                                key={i}
                                radio={40}
                                x={i*24.5 + 15}
                                y={-20}
                            />
                        )
                    }
                    {type === AuthenticationType.LOGIN ?
                        <LoginForm />
                        :
                        <SignupForm />
                    }
                </div>
            </main>
        </>
    );
}
