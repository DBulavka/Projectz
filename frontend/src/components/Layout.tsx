import { AppBar, Box, Button, Toolbar } from '@mui/material';
import { Link, Outlet } from 'react-router-dom';
import { useAuthStore } from '../store/auth';

export default function Layout() {
  const { setToken } = useAuthStore();
  return <><AppBar position='static'><Toolbar><Button color='inherit' component={Link} to='/'>Dashboard</Button><Button color='inherit' component={Link} to='/processes'>Processes</Button><Button color='inherit' component={Link} to='/instances'>Instances</Button><Button color='inherit' component={Link} to='/tasks'>My Tasks</Button><Button color='inherit' component={Link} to='/admin'>Admin</Button><Button color='inherit' onClick={() => setToken(null)}>Logout</Button></Toolbar></AppBar><Box p={2}><Outlet /></Box></>;
}
