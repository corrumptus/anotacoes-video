import { useEffect, useState } from "react";
import useUsername from "@hooks/useUsername";
import { Anotation } from "@hooks/useVideoAndAnotations";
import { numberToTime } from "@utils/numberTimeFormat";

export default function Anotations({
    anotations,
    currentAnotation,
    setCurrentTime,
    openAnotation,
    deleteAnotation,
    isOwner
}: {
    anotations: Anotation[],
    currentAnotation: number,
    setCurrentTime: (time: number) => void,
    openAnotation: (anotation: Anotation) => void,
    deleteAnotation: (anotation: Anotation) => void,
    isOwner: boolean
}) {
    const [ isMenuVisible, setIsMenuVisible ] = useState(false);
    const [ mousePosition, setMousePosition ] = useState<{
        clientX: number,
        clientY: number
    }>();
    const [ anotation, setAnotation ] = useState<Anotation>();
    const userName = useUsername();

    const canEdit = userName === anotation?.userName;
    const canDelete = isOwner || canEdit;

    useEffect(() => {
        if (!isMenuVisible)
            return;

        function onClick(e: MouseEvent) {
            const menu = document.querySelector("#anotation-manage-menu")!;

            if (menu === e.target || menu.contains(e.target as HTMLElement))
                return;

            setIsMenuVisible(false);
        }

        document.addEventListener("click", onClick);

        return () => {
            document.removeEventListener("click", onClick);
        }
    }, [isMenuVisible]);
    
    if (anotations.length === 0) return (
        <div className="h-full flex items-center justify-center">
            Vazio
        </div>
    );

    return (
        <div className="h-full flex flex-col overflow-auto">
            <ul className="flex flex-col gap-4">
                {anotations.map((a, i) =>
                    <li
                        key={a.id}
                        className={`flex gap-2 items-start relative p-2 ${i === currentAnotation ? "bg-green-300" : ""}`}
                        onContextMenu={e => {
                            e.preventDefault();
                            setAnotation(a);
                            setIsMenuVisible(true);
                            setMousePosition(e);
                        }}
                    >
                        <div className="flex flex-col items-center gap-1">
                            <div className="flex items-center justify-center w-[40px] h-[40px] overflow-hidden rounded-full bg-gray-300">
                                <img src={a.userPic} title={a.userName} />
                            </div>
                            <button
                                className="cursor-pointer text-blue-600"
                                onClick={() => setCurrentTime(a.videoInstant)}
                            >
                                {numberToTime(a.videoInstant)}
                            </button>
                        </div>
                        <div className="bg-zinc-300 p-1.5 rounded-2xl h-full w-full">
                            <span className="block h-full w-full">{a.anotation}</span>
                        </div>
                        <div className="w-[calc(100%_-_1em)] h-[1px] shadow-[0_1px_rgba(0,0,0,0.25)] absolute bottom-0" />
                    </li>
                )}
            </ul>
            {isMenuVisible &&
                <div
                    id="anotation-manage-menu"
                    className="rounded-2xl bg-white p-2 absolute flex flex-col"
                    style={{
                        top: `min(calc(100% - 64px), ${mousePosition!.clientY}px)`,
                        left: `min(calc(100% - 68px), ${mousePosition!.clientX}px)`
                    }}
                    onClick={e => {
                        e.stopPropagation();

                        setIsMenuVisible(false);
                        setMousePosition(undefined);
                        setAnotation(undefined);
                    }}
                >
                    <button
                        className={`${canEdit ? "cursor-pointer" : "cursor-default text-zinc-400"}`}
                        onClick={() => canEdit && openAnotation(anotation!)}
                    >
                        Editar
                    </button>
                    <button
                        className={`${canDelete ? "cursor-pointer" : "cursor-default text-zinc-400"}`}
                        onClick={() => canDelete && deleteAnotation(anotation!)}
                    >
                        Deletar
                    </button>
                </div>
            }
        </div>
    );
}