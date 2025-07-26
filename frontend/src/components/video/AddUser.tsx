import { useState, useEffect } from 'react';
import { FindedUser } from '@hooks/useVideoAndAnotations';

export default function AddUser({
    findUsers,
    selectUser,
    isVisible
}: {
    findUsers: (user: string) => Promise<FindedUser[]>,
    selectUser: (user: string) => void,
    isVisible: boolean
}) {
    const [ username, setUsername ] = useState("");
    const [ selectedUser, setSelectedUser ] = useState<string | undefined>(undefined);
    const [ findedUsers, setFindedUsers ] = useState<FindedUser[]>([]);

    useEffect(() => {
        if (isVisible)
            return;

        setUsername("");
        setSelectedUser(undefined);
        setFindedUsers([]);
    }, [isVisible]);

    useEffect(() => {
        const debounceTimeout = setTimeout(async () => {
            if (username === "") {
                setFindedUsers(findedUsers.filter(u => u.name === selectedUser));
                return;
            }

            setFindedUsers(await findUsers(username));
        }, 1000);

        return () => clearTimeout(debounceTimeout);
    }, [username]);

    function onClick(name: string) {
        selectUser(name);
        setSelectedUser(name);
    }

    return (
        <div className="w-[80%]">
            <div className="bg-gray-300 p-2 rounded-3xl flex flex-col gap-2">
                <input
                    placeholder="Digite um nome"
                    value={username}
                    onChange={e => setUsername(e.target.value)}
                    className="p-2 text-xl bg-gray-400 outline-none rounded-2xl"
                />
                <ul className={`${findedUsers.length === 0 ? "hidden " : ""}flex flex-col gap-1 max-h-[184px] overflow-auto`}>
                    {findedUsers.map(u =>
                        <li
                            key={u.id}
                            onClick={() => onClick(u.name)}
                            className={`flex items-center gap-2 cursor-pointer${u.name === selectedUser ? " bg-green-400" : ""} p-1 rounded-2xl`}
                        >
                            <div className="w-[40px] h-[40px] rounded-full bg-gray-400 overflow-hidden flex items-center justify-center shrink-0">
                                <img src={u.profilePic} />
                            </div>
                            <span className="text-xl overflow-hidden text-ellipsis text-nowrap">
                                {u.name}
                            </span>
                        </li>
                    )}
                </ul>
            </div>
        </div>
    );
}