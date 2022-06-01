import {
	Drawer,
	DrawerBody,
	DrawerCloseButton,
	DrawerContent,
	DrawerHeader,
	DrawerOverlay,
	DrawerProps,
	Flex,
	Icon,
	IconButton,
	useColorMode,
	Button
} from '@chakra-ui/react';
import { BiMoon, BiSun } from 'react-icons/bi';

import NavigationItem from '../NavigationItem';

type Props = Omit<DrawerProps, 'children'>;

const DrawerMenu = ({ isOpen, onClose, ...props }: Props) => {
	const { colorMode, toggleColorMode } = useColorMode();

	const onNewPageHandler = () => {
		window.location.replace('https://weather-e21e8.web.app/');
	};

	return (
		<Drawer isOpen={isOpen} placement={'right'} onClose={onClose} size={'md'} {...props}>
			<DrawerOverlay />
			<DrawerContent>
				<DrawerCloseButton />
				<DrawerHeader>Weather App</DrawerHeader>

				<DrawerBody as={Flex} flexDir={'column'} alignItems={'flex-start'} gap={2}>
					<NavigationItem path={'/'}>Home</NavigationItem>
					<Button onClick={onNewPageHandler}>Complete Description</Button>
					<IconButton
						aria-label={'Toggle dark mode'}
						colorScheme={colorMode === 'dark' ? 'secondary' : 'primary'}
						onClick={toggleColorMode}
					>
						{colorMode === 'dark' ? <Icon as={BiSun} /> : <Icon as={BiMoon} />}
					</IconButton>
				</DrawerBody>
			</DrawerContent>
		</Drawer>
	);
};

export default DrawerMenu;
