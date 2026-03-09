import { create } from 'zustand';

type State = { token: string | null; user: any | null; setToken: (t: string | null) => void; setUser: (u: any) => void };
export const useAuthStore = create<State>((set) => ({ token: localStorage.getItem('token'), user: null, setToken: (t) => { if (t) localStorage.setItem('token', t); else localStorage.removeItem('token'); set({ token: t }); }, setUser: (u) => set({ user: u }) }));
