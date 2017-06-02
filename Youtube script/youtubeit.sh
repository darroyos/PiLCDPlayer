#!/bin/bash

OUTPUT=local

####
#### v1.1 5th November 2016
####
#### youtubeit shell script for the Raspberry Pi
#### (c) Ashley Jason Scopes (SpriteMidr) under the libpng
#### license.
####
#### This script uses four components: omxplayer, youtube-dl, wget
#### and curl to search for given criteria on youtube, and then
#### actively stream the first valid result to your tty using
#### a temporary fifo.
####
#### We search youtube by constructing a curl-able url and we then
#### get the html for the search results, finding each youtube URL
#### video on the page, and we then use youtube-dl to get the urls
#### to downloadable versions of those videos. We stream it through
#### a fifo using wget as the downloader and omxplayer as the
#### player.
####
#### All files are stored on the /tmp drive and are destroyed on a
#### clean exit.
####
####

# omxplayer has no control if we are not in a terminal.
# If the user uses something like the xfce "run" window
# then they have to watch the entire video before they can
# even change tty. We don't want that. If we detect
# that we are not in a terminal, run xterm.
if [ ! -t 1 ]; then
  xterm -e "$0 $1 $2; sleep 2"
  exit 0
fi

VERBOSE=0

# Pipe to null by default
VERBOSENESS_FLAG="> /dev/null 2>&1"
VERBOSE_ERR_FLAG="2>/dev/null"

function usage() {
  echo "USAGE: $0 'some search term' [-v]" 
  echo "  This script will search Youtube for a given video by"
  echo "  specifying a search string. If it can find a result, it"
  echo "  will play the video using omxplayer."
  echo
  echo "  -v  be super verbose and output lots of text D:"
  echo
  echo "This script is licensed under libpng. It would be awesome if you"
  echo "could credit me though :)"
  echo "Check me out at github.com/spritemidr"
  echo
}

if [[ $1 =~ ^(-h|--help)$ ]];
  then
  usage
  exit 0
elif [[ $1 =~ ^\s*$ ]]; then
  echo You need to enter something. You cannot just enter nothing, or a
  echo string of spaces. Be reasonable\! \>:\(
  echo
  echo ...I will tell you what: I will be helpful and show you the usage
  echo message.
  echo
  usage
  exit 1
fi
if [[ $2 -eq "-v" ]];
then
  echo GONNA BE SUPER VERBOSE, YALL\!
  VERBOSE=1
  VERBOSENESS_FLAG=""
  VERBOSE_ERR_FLAG=""
fi

function ensure-directories-exist() {
  mkdir -pvm 777 /tmp/youtubeit/search /tmp/youtubeit/fifo $VERBOSENESS_FLAG
  if [[ $? != 0 ]];
    then
    echo There were issues accessing the /tmp partition/directory. Are you
    echo sure you have privileges to access and write to it? :-\(
    echo
    exit 2
  fi
  return 0
}

ensure-directories-exist

echo Searching Youtube for "$1"

# Gotta escape spaces, I should escape all HTML
# chars, but I cannot be bothered today.
SEARCH_TERM="$(sed "s/ /%20/g" <<< $1)"
echo Escaped input to $SEARCH_TERM $VERBOSENESS_FLAG
echo Searching Youtube... be patient\! $VERBOSENESS_FLAG

# Get the search results, find the watch strings, then remove any duplicates, and con-
# catenate the full URL to them, before storing them in the "SEARCH_RESULTS" variable
SEARCH_RESULTS=""
SEARCH_URL="https://www.youtube.com/results?search_query=$SEARCH_TERM"

echo Getting results from $SEARCH_URL $VERBOSENESS_FLAG
SEARCH_RESULTS_ORIGINAL=`curl -L $SEARCH_URL $VERBOSE_ERR_FLAG`

if [[ $? != 0 ]];
then
  echo There was an error getting search results. Please try again later.
  echo
  exit 5
fi

# If the user wanted verbose output, we will also grep the
# names of each video. We might wanna use this in the future
# if we make the script interactive!
for result in `grep -oP 'watch\?[\w=-_$%]{10,15}' <<< $SEARCH_RESULTS_ORIGINAL | uniq`;
  do
  if [[ $VERBOSE -eq 1 ]];
    then
    echo "Found result: $result"
  fi

  SEARCH_RESULTS="$SEARCH_RESULTS https://youtube.com/$result"
done

# Reseparate with newlines
SEARCH_RESULTS=`tr ' ' '\n' <<< $SEARCH_RESULTS`


for VIDEO_URL in $SEARCH_RESULTS;
  do

  echo
  echo Going to play $VIDEO_URL

  # Make fifo to store stream in
  FIFO_NAME="/tmp/youtubeit/fifo/`date +%s%N`"
  echo Storing stream in FIFO $FIFO_NAME
  mkfifo $FIFO_NAME $VERBOSENESS_FLAG &

  # Get the URL of the video so that we can
  # download it
  ACTUAL_VIDEO_URL=`youtube-dl -ig $VIDEO_URL $VERBOSE_ERR_FLAG`

  if [[ $? != 0 ]];
    then
    echo Error with $VIDEO_URL
    rm $FIFO_NAME
    continue
  fi

  echo Beginning download now\!
  echo Remember, press "q" to quit, or see "man omxplayer" for more help\!
  # Wget the video, storing it in the fifo
  wget -O $FIFO_NAME $ACTUAL_VIDEO_URL & $VERBOSENESS_FLAG
  if [[ $? != 0 ]];
    then
    echo Error accessing $VIDEO_URL
    rm $FIFO_NAME
    continue
  fi
  
  omxplayer -r -o $OUTPUT $FIFO_NAME $VERBOSENESS_FLAG
  rm $FIFO_NAME $VERBOSENESS_FLAG
  break
done

# Cleanup
# Prevents the tmp folder being clogged up. If the user kills the
# process or it crashes, we may end up with fifos cluttering our
# tmp folder.
rm /tmp/youtubeit/fifo/* -f & > /dev/null $VERBOSE_ERR_FLAG
