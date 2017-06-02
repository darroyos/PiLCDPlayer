#!/usr/bin/python

import sys
sys.path.insert(0, 'libs/')
import I2C_LCD_driver
import time
import os
import threading
import signal

# Constants
NUM_ARGUMENTS = 3

class LCDJob(threading.Thread):

    def __init__(self, videoTitle):
        threading.Thread.__init__(self)

        # The shutdown_flag is a threading.Event object that
        # indicates whether the thread should be terminated.
        self.shutdown_flag = threading.Event()

        # ... Other thread setup code here ...
        self.videoTitle = videoTitle

    def run(self):
            print('Thread #%s started' % self.ident)

            while not self.shutdown_flag.is_set():
                # ... Job code here ...
                time.sleep(0.5)

                mylcd = I2C_LCD_driver.lcd()

                str_pad = " " * 16
                my_long_string = str_pad + self.videoTitle

                while True and not self.shutdown_flag.is_set():
                    for i in range (0, len(my_long_string)):
                        lcd_text = my_long_string[i:(i+16)]
                        mylcd.lcd_display_string(lcd_text,1)
                        time.sleep(0.3)
                        mylcd.lcd_display_string(str_pad,1)

                        if self.shutdown_flag.is_set():
                            break

            # ... Clean shutdown code here ...
            print('Thread #%s stopped' % self.ident)

class YoutubeJob(threading.Thread):

    def __init__(self, videoTitle):
        threading.Thread.__init__(self)

        # The shutdown_flag is a threading.Event object that
        # indicates whether the thread should be terminated.
        self.shutdown_flag = threading.Event()

        # ... Other thread setup code here ...
        self.videoTitle = videoTitle

    def run(self):
            print('Thread #%s started' % self.ident)

            os.system("youtubeit " + "\"" + self.videoTitle + "\"")

            # ... Clean shutdown code here ...
            print('Thread #%s stopped' % self.ident)

class ServiceExit(Exception):
    """
    Custom exception which is used to trigger the clean exit
    of all running threads and the main program.
    """
    pass


def service_shutdown(signum, frame):
    print('Caught signal %d' % signum)
    raise ServiceExit

def main():

    # Register the signal handlers
    signal.signal(signal.SIGTERM, service_shutdown)
    signal.signal(signal.SIGINT, service_shutdown)

    args_size = len(sys.argv)
    print("Number of arguments: " + str(args_size))

    def usage():
        print("Correct syntax to play a Youtube video:")
        print("\tpilcd -t \"title of the video\"")


    if len(sys.argv) != NUM_ARGUMENTS:
        usage()
    else:
        title = sys.argv[2] # title to display on the LCD

        try:
            lcd_thread = LCDJob(title)
            lcd_thread.start()
            youtube_thread = YoutubeJob(title)
            youtube_thread.start()

            # Keep the main thread running, otherwise signals are ignored.
            while True:
                time.sleep(0.5)

        except ServiceExit:
            # Terminate the running threads.
            # Set the shutdown flag on each thread to trigger a clean shutdown of each thread.
            lcd_thread.shutdown_flag.set()
            # Wait for the threads to close...
            lcd_thread.join()
            youtube_thread.join()

    print('Exiting main program')

if __name__ == '__main__':
    main()
