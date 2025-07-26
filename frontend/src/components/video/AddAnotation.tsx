import { useEffect, useState } from "react";
import TimeInput from "@components/video/TimeInput";
import Argola from "@components/utils/Argola";
import { AnotationPublicFields } from "@hooks/useVideoAndAnotations";

export default function AddAnotation({
    currentInstant,
    maxTime,
    isVisible,
    close,
    add,
    isAdding
}: {
    currentInstant: number,
    maxTime: number,
    isVisible: boolean,
    close: () => void,
    add: (newAnotation: AnotationPublicFields) => void,
    isAdding: boolean
}) {
    const [ newAnotation, setNewAnotation ] = useState<AnotationPublicFields>({
        anotation: "",
        videoInstant: currentInstant
    });
    const [ argolas, setArgolas ] = useState<{y: number, amount: undefined[]}>({y: 0, amount: []});

    useEffect(() => {
        function onResize() {
            const { clientWidth, clientHeight } = document.querySelector("#add-video-modal-parent-element div")!;

            setArgolas({
                amount: new Array(Math.floor(clientWidth/30)).fill(undefined),
                y: -clientHeight/2
            });
        }

        window.addEventListener("resize", onResize);

        onResize();

        return () => {
            window.removeEventListener("resize", onResize);
        }
    }, []);

    useEffect(() => {
        if (!isVisible)
            setNewAnotation({ anotation: "", videoInstant: currentInstant });
    }, [isVisible]);

    return (
        <div
            id="add-video-modal-parent-element"
            className={`absolute top-0 h-full w-full bg-black/50 flex items-center justify-center ${isVisible ? "opacity-100 z-1" : "opacity-0 -z-1"}`}
            onClick={e => {
                e.stopPropagation();

                if ((e.target as HTMLDivElement).id !== "add-video-modal-parent-element")
                    return;

                close();
            }}
        >
            <div className="h-[58%] w-[65%] bg-[#ffffbd] flex flex-col items-stretch justify-center gap-2.5 p-6 content-stretch rounded-3xl">
                {argolas.amount.map((_, i) =>
                    <Argola
                        key={i}
                        radio={40}
                        x={i*29}
                        y={argolas.y}
                    />
                )}
                <div className="absolute top-[calc(21%_+_24px)] right-[calc(17.5%_+_24px)]">
                    <button onClick={close}>
                        <img
                            src="https://images.icon-icons.com/2518/PNG/512/x_icon_150997.png"
                            alt="fechar(x)"
                            className="w-[40px] h-[40px] cursor-pointer"
                        />
                    </button>
                </div>
                <header className="text-2xl font-bold text-center">
                    Adicione uma nova anotação
                </header>
                <div className="flex flex-col gap-2">
                    <label
                        className="border-b border-red-600"
                        htmlFor="anotacao"
                    >
                        Anotação
                    </label>
                    <textarea
                        id="anotacao"
                        value={newAnotation.anotation}
                        placeholder=" "
                        onChange={e => setNewAnotation(cur => ({...cur, anotation: e.target.value}))}
                        className="text-xl underline decoration-red-600 underline-offset-4 outline-none resize-none placeholder-shown:border-b placeholder-shown:border-red-600 placeholder-shown:no-underline"
                    />
                </div>
                <div className="flex flex-col gap-2">
                    <label
                        className="border-b border-red-600"
                        htmlFor="anotacao"
                    >
                        Momento
                    </label>
                    <TimeInput
                        initialTime={currentInstant}
                        maxTime={maxTime}
                        setTime={n => setNewAnotation(cur => ({...cur, videoInstant: n}))}
                    />
                </div>
                <div className="border-b border-red-600 py-1">
                    <button
                        className={`${isAdding ? "bg-gray-600 cursor-wait" : "bg-lime-400"} rounded-2xl px-2 py-0.5 transition-colors transition-700 cursor-pointer outline-none`}
                        onClick={() => {
                            if (isAdding)
                                return;

                            add(newAnotation);
                            close();
                        }}
                    >
                        Adicionar
                    </button>
                </div>
            </div>
        </div>
    );
}