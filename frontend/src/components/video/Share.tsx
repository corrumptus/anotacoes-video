import { VideoVisibility, VisibilityType } from "@hooks/useVideoStore";
import { visibilityToString } from "@utils/toString";

export default function Share({
    visibility,
    changeVisibility,
    changeCanAnotate,
    isChangingVisibility,
    removeUser
}: {
    visibility: VideoVisibility,
    changeVisibility: (newVisibility: VisibilityType) => void,
    changeCanAnotate: (canAnotate: boolean, visitant: string | undefined) => void,
    isChangingVisibility: boolean,
    removeUser: (user: string) => void
}) {
    let children = null;

    if (visibility.type === VisibilityType.RESTRICTED) children = (
        <div className="h-full flex flex-col overflow-hidden">
            <span className="text-xl">Visitantes</span>
            <ul className="flex flex-col gap-2 overflow-auto">
                {visibility.canAnotateVisitants
                    ?.map((user, i) =>
                        <li
                            key={user.name}
                            className={`p-1 rounded-2xl flex justify-between ${i%2 === 0 ? "bg-green-300" : "bg-green-200"}`}
                        >
                            <div className="flex items-center gap-1">
                                <div
                                    onClick={() => changeCanAnotate(false, user.name)}
                                    className="flex items-center justify-center shrink-0 w-[40px] h-[40px] overflow-hidden rounded-full bg-gray-400"
                                >
                                    <img src={user.profilePic} />
                                </div>
                                <label className="cursor-text" htmlFor={user.name}>{user.name}</label>
                            </div>
                            <input
                                type="checkbox"
                                id={user.name}
                                checked={true}
                                onChange={() => changeCanAnotate(false, user.name)}
                                readOnly={isChangingVisibility}
                                className="cursor-pointer"
                            />
                            <div
                                onClick={() => removeUser(user.name)}
                                className="shrink-0 w-[40px] h-[40px] p-1 cursor-pointer"
                            >
                                <img
                                    src="https://images.vexels.com/media/users/3/223479/isolated/preview/8ecc75c9d0cf6d942cce96e196d4953f-icone-da-lixeira-plana.png"
                                    alt="remover usuario(lixo)"
                                />
                            </div>
                        </li>
                    )
                }
                {visibility.canNotAnotateVisitants
                    ?.map((user, i) =>
                        <li
                            key={user.name}
                            className={`p-1 rounded-2xl flex justify-between ${i%2 === 0 ? "bg-red-300" : "bg-red-200"}`}
                        >
                            <div className="flex items-center gap-1">
                                <div
                                    onClick={() => changeCanAnotate(true, user.name)}
                                    className="flex items-center justify-center shrink-0 w-[40px] h-[40px] overflow-hidden rounded-full bg-gray-400"
                                >
                                    <img src={user.profilePic} />
                                </div>
                                <label className="cursor-text" htmlFor={user.name}>{user.name}</label>
                            </div>
                            <input
                                type="checkbox"
                                id={user.name}
                                checked={false}
                                onChange={() => changeCanAnotate(true, user.name)}
                                readOnly={isChangingVisibility}
                                className="cursor-pointer"
                            />
                            <div
                                onClick={() => removeUser(user.name)}
                                className="shrink-0 w-[40px] h-[40px] p-1 cursor-pointer"
                            >
                                <img
                                    src="https://images.vexels.com/media/users/3/223479/isolated/preview/8ecc75c9d0cf6d942cce96e196d4953f-icone-da-lixeira-plana.png"
                                    alt="remover usuario(lixo)"
                                />
                            </div>
                        </li>
                    )
                }
            </ul>
        </div>
    );

    if (visibility.type === VisibilityType.PUBLIC) children = (
        <div>
            <span className="text-xl">Visitantes podem fazer anotações?</span>
            <div className="flex gap-2">
                <label htmlFor="visitants">Visitantes</label>
                <input
                    type="checkbox"
                    id="visitants"
                    checked={visibility.canAnotateVisitants !== null ? true : false}
                    onChange={() => changeCanAnotate(visibility.canAnotateVisitants !== null ? false : true, undefined)}
                    readOnly={isChangingVisibility}
                />
            </div>
        </div>
    );

    return (
        <>
            <div className="flex flex-col items-center gap-2">
                <label className="text-xl cursor-text">Visibilidade</label>
                <select
                    value={visibility.type}
                    onChange={e => changeVisibility(e.target.value as VisibilityType)}
                    disabled={isChangingVisibility}
                    className="bg-slate-400 p-2 rounded-3xl text-xl"
                >
                    <option value={VisibilityType.PRIVATE}>
                        {visibilityToString(VisibilityType.PRIVATE)}
                    </option>
                    <option value={VisibilityType.RESTRICTED}>
                        {visibilityToString(VisibilityType.RESTRICTED)}
                    </option>
                    <option value={VisibilityType.PUBLIC}>
                        {visibilityToString(VisibilityType.PUBLIC)}
                    </option>
                </select>
            </div>
            {children}
        </>
    );
}