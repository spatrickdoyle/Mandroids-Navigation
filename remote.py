import curses,sys

if len(sys.argv) < 2:
	print "need a power arg"
	raise SystemExit

screen = curses.initscr()
screen.keypad(1)
screen.addstr("something")
screen.refresh()

while True:
	k = screen.getch()
	if k == 113:
		curses.endwin()
		break
	if k == curses.KEY_UP:
		print sys.argv[1],sys.argv[1]
	if k == curses.KEY_DOWN:
		print '-'+sys.argv[1],'-'+sys.argv[1]
	if k == curses.KEY_LEFT:
		print '-'+sys.argv[1],sys.argv[1]
	if k == curses.KEY_RIGHT:
		print sys.argv[1],'-'+sys.argv[1]
